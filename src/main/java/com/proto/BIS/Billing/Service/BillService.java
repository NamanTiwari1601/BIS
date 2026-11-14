package com.proto.BIS.Billing.Service;

import com.proto.BIS.Billing.DTO.BillItemRequestDTO;
import com.proto.BIS.Billing.DTO.BillItemResponseDTO;
import com.proto.BIS.Billing.DTO.BillRequestDTO;
import com.proto.BIS.Billing.DTO.BillResponseDTO;
import com.proto.BIS.Billing.Model.BillItemModel;
import com.proto.BIS.Billing.Model.BillItemStockModel;
import com.proto.BIS.Billing.Model.Bills;
import com.proto.BIS.Billing.Model.DuesModel;
import com.proto.BIS.Billing.Repository.DuesRepo;
import com.proto.BIS.Package.Model.PackageModel;
import com.proto.BIS.Package.Repository.PackageRepository;
import com.proto.BIS.Billing.Repository.BillRepo;
import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Model.ServiceProduct;
import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Repository.ProductRepo;
import com.proto.BIS.common.Repository.ServiceProductRepository;
import com.proto.BIS.common.Repository.UserRepo;
import com.proto.BIS.common.Repository.WorkRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillService {

    public final BillRepo billRepo;

    public final ProductRepo productRepo;

    public final WorkRepo serviceRepo;

    public final PackageRepository packageRepo;

    public final ServiceProductRepository serviceProductRepository;

    public final UserRepo userRepo;
    public  final DueService dueService;
    public final DuesRepo duesRepo;

    @Transactional(rollbackFor = Exception.class)
    public BillResponseDTO createBill(BillRequestDTO request, String userName){
        log.info("In Create Bill");
        double total=0;

            Bills bills = new Bills();
            log.info("UserId:-{}",request.getStaffId());
           List <BillItemModel> billItems = new ArrayList<>();
        UserModel user = userRepo.findById(request.getStaffId())
                .orElseThrow(() -> {
                    log.warn("Staff not found with id:{}",request.getStaffId());
                    return new IllegalArgumentException("Staff does not  exist");
                });
        bills.setStaff(user);
        log.info("Client Name:-{}",request.getClientName());
        bills.setClientName(request.getClientName());
        bills.setClientPhNo(request.getClientPhone());
        log.info("Payment Mode:-{}",request.getPaymentMode());
        bills.setPaymentMode(request.getPaymentMode());
        bills.setEmailId(request.getClientEmail());
        bills.setNotes(request.getNotes());
        bills.setInvoiceId(request.getInvoiceId());
        UserModel creator = userRepo.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user was not found"));
        LocalDate billDate = creator.isUserAdmin() && request.getBillDate() != null
                ? request.getBillDate()
                : LocalDate.now();
        bills.setBillDate(billDate.atStartOfDay());
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one bill item is required");
        }
        for (BillItemRequestDTO item : request.getItems()) {
            boolean hasProduct = item.getProductId() != null;
            boolean hasService = item.getServiceId() != null;
            boolean hasPackage = item.getPackageId() != null;

            BillItemModel billItem=new BillItemModel();
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Bill item quantity must be greater than zero");
            }
            if ((hasProduct ? 1 : 0) + (hasService ? 1 : 0) + (hasPackage ? 1 : 0) != 1) {
                throw new IllegalArgumentException("Each bill item must reference exactly one of product, service, or package");
            }
            double subtotal;
            if (hasProduct) {
                log.info("In Product Section");
                log.info("ProductId:-{}",item.getProductId());
                ProductModel product = productRepo.findById(item.getProductId())
                        .orElseThrow(() -> {
                            log.error("Product not Found");
                            return new IllegalArgumentException("Product not found");
                        });

                long availableQuantity = product.getProductQuantity() == null ? 0 : product.getProductQuantity();
                if (availableQuantity < item.getQuantity()) {
                    log.error("Insufficient stock for product: {}. Avaliable: {}", product.getProductName(), availableQuantity);
                    throw new IllegalArgumentException("Insufficient stock for product: " + product.getProductName()
                            + ". Avaliable: " + availableQuantity);
                }
                double price = product.getProductPrice();
                subtotal = price * item.getQuantity();
                log.info("Subtotal:-{}",subtotal);
                Long prevQuantity = product.getProductQuantity();
                Long newQuantity = prevQuantity - (long) item.getQuantity();
                product.setProductQuantity(newQuantity);
                productRepo.save(product);
                billItem.setProduct(product);
                billItem.setItemNameSnapshot(product.getProductName());
                billItem.setUnitPriceSnapshot(price);
                billItem.setQuantity(item.getQuantity());
                log.info("Old Quantity:-{}",prevQuantity);
                log.info("New Quantity:-{}",newQuantity);
            } else if (hasService) {
                log.info("In Services Section");
                log.info("ServiceID:-{}",item.getServiceId());
                ServicesModel service = serviceRepo.findById(item.getServiceId())
                        .orElseThrow(() -> {
                            log.error("Services not found");
                            return new IllegalArgumentException("Services not Found");});
                double price = service.getServicePrice();
                subtotal = price * item.getQuantity();
                billItem.setServices(service);
                billItem.setItemNameSnapshot(service.getServiceName());
                billItem.setUnitPriceSnapshot(price);
                billItem.setQuantity(item.getQuantity());
                billItem.setStockUsageCaptured(true);
                billItem.setStockConsumptions(adjustLinkedProductStock(service, item.getQuantity(), false, billItem));
                log.info("Subtotal:-{}",subtotal);
            } else {
                PackageModel pack = packageRepo.findById(item.getPackageId())
                        .orElseThrow(() -> new IllegalArgumentException("Package not found: " + item.getPackageId()));
                if (!"ACTIVE".equalsIgnoreCase(pack.getStatus())) {
                    throw new IllegalArgumentException("Package is not active: " + pack.getPackageName());
                }
                double price = pack.getPackagePrice();
                subtotal = price * item.getQuantity();
                billItem.setPackageModel(pack);
                billItem.setItemNameSnapshot(pack.getPackageName());
                billItem.setUnitPriceSnapshot(price);
                billItem.setQuantity(item.getQuantity());
            }
            billItem.setSubtotal(subtotal);
            billItem.setBill(bills);
            billItems.add(billItem);
            total += subtotal;
        }

        log.info("Total:-{}",total);
        double dis = request.getDiscountAmount() == null ? 0 : request.getDiscountAmount();
        log.info("Discount Amount:-{}",dis);
        total= total-dis;
        double stpr = request.getGstPercent() == null ? 0 : request.getGstPercent();
        log.info("GST Percent:-{}",stpr);
        total= total+((stpr/100)*total);
        log.info("Total after Discount and GST:-{}",total);
        if("Mixed".equalsIgnoreCase(request.getPaymentMode())){
            if(request.getCashAmount()== null|| request.getOnlineAmount()==null){
                log.error("Both cash And online amount should  be present");
                throw new IllegalArgumentException("Mixed Requires both cash and online payments");
            }
            bills.setCashAmount(request.getCashAmount());
            bills.setOnlineAmount(request.getOnlineAmount());

        }
        else{
            bills.setOnlineAmount(null);
            bills.setCashAmount(null);
        }

        bills.setBillAmount(total);
        bills.setDiscountAmount(dis);
        bills.setGstPercent(stpr);
        bills.setItems(billItems);
        if ("Due".equalsIgnoreCase(request.getPaymentStatus()) && request.getDueAmount() != null && request.getDueAmount() > 0) {
            log.info("Creating due for bill: billId={}, dueAmount={}", bills.getBillId(), request.getDueAmount());
            bills.setBillDues(request.getDueAmount());
            bills.setIsDue(true);
        } else {
            bills.setBillDues(0.0);
            bills.setIsDue(false);
        }

        billRepo.saveAndFlush(bills);
        if ("Due".equalsIgnoreCase(request.getPaymentStatus()) && request.getDueAmount() != null && request.getDueAmount() > 0) {
            double amountPaid = total - request.getDueAmount();
            LocalDate dueDate = request.getDueDate() != null ? request.getDueDate() : LocalDate.now();
            dueService.createDuesFromBills(bills, amountPaid, dueDate, request.getNotes());
        }


        return mapToResponseDTO(bills);
    }

    @Transactional(rollbackFor = Exception.class)
    public BillResponseDTO updateBill(int billId, BillRequestDTO request, String userName) {
        requireAdmin(userName);
        Bills bill = billRepo.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found with id: " + billId));
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("At least one bill item is required");
        }
        if (request.getBillDate() == null) {
            throw new IllegalArgumentException("Bill date is required");
        }
        if (request.getClientName() == null || request.getClientName().isBlank()) {
            throw new IllegalArgumentException("Client name is required");
        }
        if (request.getPaymentMode() == null || request.getPaymentMode().isBlank()) {
            throw new IllegalArgumentException("Payment mode is required");
        }

        List<DuesModel> existingDues = duesRepo.findByBill_BillId(billId);
        if (existingDues.size() > 1) {
            throw new IllegalStateException("This bill has multiple dues records and cannot be edited safely");
        }

        adjustBillStock(bill, true);
        List<BillItemModel> updatedItems = createUpdatedItems(bill, request.getItems());
        double subtotal = updatedItems.stream().mapToDouble(BillItemModel::getSubtotal).sum();
        double discount = request.getDiscountAmount() == null ? 0 : request.getDiscountAmount();
        double gstPercent = request.getGstPercent() == null ? 0 : request.getGstPercent();
        double dueAmount = request.getDueAmount() == null ? 0 : request.getDueAmount();
        if (!Double.isFinite(discount) || discount < 0 || discount > subtotal) {
            throw new IllegalArgumentException("Discount must be between zero and the items subtotal");
        }
        if (!Double.isFinite(gstPercent) || gstPercent < 0 || gstPercent > 100) {
            throw new IllegalArgumentException("GST percent must be between zero and 100");
        }
        double total = (subtotal - discount) * (1 + gstPercent / 100);
        if (!Double.isFinite(dueAmount) || dueAmount < 0 || dueAmount > total) {
            throw new IllegalArgumentException("Due amount must be between zero and the bill total");
        }
        if ("Mixed".equalsIgnoreCase(request.getPaymentMode())) {
            Double cash = request.getCashAmount();
            Double online = request.getOnlineAmount();
            if (cash == null || online == null || !Double.isFinite(cash) || !Double.isFinite(online)
                    || cash <= 0 || online <= 0
                    || Math.round((cash + online) * 100) != Math.round((total - dueAmount) * 100)) {
                throw new IllegalArgumentException("Cash and online amounts must be positive and sum to the amount collected");
            }
            bill.setCashAmount(cash);
            bill.setOnlineAmount(online);
        } else {
            bill.setCashAmount(null);
            bill.setOnlineAmount(null);
        }

        if (request.getStaffId() == null) {
            throw new IllegalArgumentException("Staff is required");
        }
        UserModel staff = userRepo.findById(request.getStaffId())
                .orElseThrow(() -> new IllegalArgumentException("Staff does not exist"));
        bill.setStaff(staff);
        bill.setClientName(request.getClientName().trim());
        bill.setClientPhNo(request.getClientPhone());
        bill.setEmailId(request.getClientEmail());
        bill.setNotes(request.getNotes());
        bill.setPaymentMode(request.getPaymentMode());
        bill.setBillDate(request.getBillDate().atStartOfDay());
        bill.setBillAmount(total);
        bill.setDiscountAmount(discount);
        bill.setGstPercent(gstPercent);
        bill.setBillDues(dueAmount);
        bill.setIsDue(dueAmount > 0);

        if (request.getInvoiceId() != null && !request.getInvoiceId().isBlank()) {
            bill.setInvoiceId(request.getInvoiceId());
        }
        if (bill.getItems() == null) {
            bill.setItems(new ArrayList<>());
        } else {
            bill.getItems().clear();
        }
        bill.getItems().addAll(updatedItems);

        if (dueAmount > 0 || !existingDues.isEmpty()) {
            LocalDate dueDate = request.getDueDate() != null ? request.getDueDate() : LocalDate.now();
            if (existingDues.isEmpty()) {
                dueService.createDuesFromBills(bill, total - dueAmount, dueDate, request.getNotes());
            } else {
                updateDues(existingDues.get(0), bill, total, dueAmount, dueDate, request.getNotes());
            }
        }

        return mapToResponseDTO(billRepo.save(bill));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteBill(int billId, boolean restoreStock, boolean restoreDues, String userName) {
        requireAdmin(userName);
        Bills bill = billRepo.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found with id: " + billId));

        if (restoreStock) {
            adjustBillStock(bill, true);
        }

        List<DuesModel> dues = duesRepo.findByBill_BillId(billId);
        if (restoreDues) {
            duesRepo.deleteAll(dues);
        } else {
            for (DuesModel due : dues) {
                due.setBill(null);
            }
            duesRepo.saveAll(dues);
        }
        billRepo.delete(bill);
    }

    private void requireAdmin(String userName) {
        UserModel user = userRepo.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user was not found"));
        if (!user.isUserAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can edit or delete bills");
        }
    }

    private List<BillItemModel> createUpdatedItems(Bills bill, List<BillItemRequestDTO> items) {
        List<BillItemModel> updatedItems = new ArrayList<>();
        for (BillItemRequestDTO requestItem : items) {
            boolean hasProduct = requestItem.getProductId() != null;
            boolean hasService = requestItem.getServiceId() != null;
            boolean hasPackage = requestItem.getPackageId() != null;
            if ((hasProduct ? 1 : 0) + (hasService ? 1 : 0) + (hasPackage ? 1 : 0) != 1) {
                throw new IllegalArgumentException("Each bill item must reference exactly one of product, service, or package");
            }
            if (requestItem.getQuantity() == null || requestItem.getQuantity() <= 0) {
                throw new IllegalArgumentException("Bill item quantity must be greater than zero");
            }
            if (requestItem.getUnitPrice() == null || !Double.isFinite(requestItem.getUnitPrice())
                    || requestItem.getUnitPrice() < 0) {
                throw new IllegalArgumentException("Bill item unit price must be zero or greater");
            }

            BillItemModel item = new BillItemModel();
            item.setBill(bill);
            item.setQuantity(requestItem.getQuantity());
            item.setUnitPriceSnapshot(requestItem.getUnitPrice());
            double subtotal = requestItem.getUnitPrice() * requestItem.getQuantity();
            if (!Double.isFinite(subtotal)) {
                throw new IllegalArgumentException("Bill item subtotal is invalid");
            }

            if (hasProduct) {
                ProductModel product = productRepo.findById(requestItem.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"));
                adjustProductStock(product, requestItem.getQuantity(), false);
                item.setProduct(product);
                item.setItemNameSnapshot(product.getProductName());
            } else if (hasService) {
                ServicesModel service = serviceRepo.findById(requestItem.getServiceId())
                        .orElseThrow(() -> new IllegalArgumentException("Service not found"));
                item.setStockUsageCaptured(true);
                item.setStockConsumptions(adjustLinkedProductStock(service, requestItem.getQuantity(), false, item));
                item.setServices(service);
                item.setItemNameSnapshot(service.getServiceName());
            } else {
                PackageModel pack = packageRepo.findById(requestItem.getPackageId())
                        .orElseThrow(() -> new IllegalArgumentException("Package not found"));
                item.setPackageModel(pack);
                item.setItemNameSnapshot(pack.getPackageName());
            }
            item.setSubtotal(subtotal);
            updatedItems.add(item);
        }
        return updatedItems;
    }

    private void updateDues(DuesModel due, Bills bill, double total, double dueAmount, LocalDate dueDate, String notes) {
        double paidAmount = total - dueAmount;
        due.setClientName(bill.getClientName());
        due.setClientPhone(bill.getClientPhNo());
        due.setTotalAmount(total);
        due.setPaidAmount(paidAmount);
        due.setDueAmount(dueAmount);
        due.setDuedate(dueDate);
        due.setNotes(notes);
        due.setStatus(dueAmount <= 0 ? "PAID" : paidAmount <= 0 ? "PENDING" : "PARTIAL");
        duesRepo.save(due);
    }

    private void adjustBillStock(Bills bill, boolean restore) {
        if (bill.getItems() == null) return;
        for (BillItemModel item : bill.getItems()) {
            if (item.getProduct() != null) {
                adjustProductStock(item.getProduct(), item.getQuantity(), restore);
            } else if (item.getServices() != null) {
                if (item.isStockUsageCaptured()) {
                    for (BillItemStockModel consumption : item.getStockConsumptions()) {
                        adjustProductStock(consumption.getProduct(), consumption.getQuantity(), restore);
                    }
                } else {
                    adjustLinkedProductStock(item.getServices(), item.getQuantity(), restore, null);
                }
            }
        }
    }

    private void adjustProductStock(ProductModel product, long quantity, boolean restore) {
        long current = product.getProductQuantity() == null ? 0 : product.getProductQuantity();
        long adjusted = restore ? current + quantity : current - quantity;
        if (adjusted < 0) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getProductName()
                    + ". Available: " + current);
        }
        product.setProductQuantity(adjusted);
        productRepo.save(product);
    }

    private List<BillItemStockModel> adjustLinkedProductStock(
            ServicesModel service, Integer serviceQuantity, boolean restore, BillItemModel billItem) {
        List<ServiceProduct> linkedProducts = serviceProductRepository
                .findByService_ServiceId(service.getServiceId());
        List<BillItemStockModel> consumptions = new ArrayList<>();

        for (ServiceProduct linkedProduct : linkedProducts) {
            ProductModel product = linkedProduct.getProduct();
            Integer requiredQuantity = linkedProduct.getRequiredQty();
            Long availableQuantity = product.getProductQuantity();

            if (availableQuantity == null) {
                availableQuantity = 0L;
            }

            if (requiredQuantity == null || requiredQuantity < 0) {
                log.warn("Invalid required quantity for product {} linked to service {}: {}",
                        product.getProductName(), service.getServiceName(), requiredQuantity);
                throw new IllegalArgumentException("Invalid required quantity for product "
                        + product.getProductName() + " linked to service " + service.getServiceName());
            }

            long quantityToAdjust = Math.multiplyExact((long) requiredQuantity, serviceQuantity);
            if (quantityToAdjust < 0 || (!restore && availableQuantity < quantityToAdjust)) {
                log.warn("Insufficient stock for product {} required by service {}. Available: {}, required: {}",
                        product.getProductName(), service.getServiceName(), availableQuantity, quantityToAdjust);
                throw new IllegalArgumentException("Insufficient stock for product " + product.getProductName()
                        + " required by service " + service.getServiceName()
                        + ". Available: " + availableQuantity + ", required: " + quantityToAdjust);
            }

            Long newQuantity = restore ? availableQuantity + quantityToAdjust : availableQuantity - quantityToAdjust;
            product.setProductQuantity(newQuantity);
            productRepo.save(product);
            if (!restore && quantityToAdjust > 0) {
                BillItemStockModel consumption = new BillItemStockModel();
                consumption.setBillItem(billItem);
                consumption.setProduct(product);
                consumption.setQuantity(quantityToAdjust);
                consumptions.add(consumption);
            }
            log.info("{} {} units of product {} for service {} x{}; remaining stock: {}",
                    restore ? "Restored" : "Removed", quantityToAdjust, product.getProductName(),
                    service.getServiceName(), serviceQuantity, newQuantity);
        }
        return consumptions;
    }

    public List<BillResponseDTO> getTodaysBills(){
        log.info("In getTodaysBills");
        LocalDateTime start= LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return billRepo.findByBillDateBetween(start, end).stream()
                .map(this::mapToResponseDTO).toList();
    }
    public List<BillResponseDTO> getAllBills(){
        log.info("In getAllBills");
        List<BillResponseDTO> billsList=billRepo.findAll().stream()
                .map(this::mapToResponseDTO).toList();
        return billsList;


    }
    public BillResponseDTO  getBillById(int billId){
        log.info("In getBillById");
        Bills bill= billRepo.findById(billId).orElseThrow(() -> new IllegalArgumentException("Bill not Found with id: "+billId));
        return mapToResponseDTO(bill);
    }

    public List<BillResponseDTO> getFilteredBills(Integer staffId, LocalDate from, LocalDate to){
        log.info("In get filtered bills: staffId={}, from={}, to={}", staffId, from, to);
        LocalDateTime start=   (from != null ? from : LocalDate.of(2000,1,1)).atStartOfDay();
        LocalDateTime end = (to != null ? to : LocalDate.now()).atStartOfDay();

        List<Bills> bills;
        if (staffId !=null){
           bills= billRepo.findByStaffUserIdAndBillDateBetween(staffId,start,end);
        }
        else{
            bills= billRepo.findByBillDateBetween(start,end);
        }

        return bills.stream().map(this::mapToResponseDTO).toList();
    }

    public Page<BillResponseDTO> getFilteredBills(Integer staffId, LocalDate from, LocalDate to, Pageable pageable){
        log.info("In get paginated filtered bills: staffId={}, from={}, to={}, page={}, size={}",
                staffId, from, to, pageable.getPageNumber(), pageable.getPageSize());
        LocalDateTime start = (from != null ? from : LocalDate.of(2000,1,1)).atStartOfDay();
        LocalDateTime end = (to != null ? to : LocalDate.now()).atTime(23,59);

        Page<Bills> billsPage;
        if (staffId != null) {
            billsPage = billRepo.findByStaffUserIdAndBillDateBetween(staffId, start, end, pageable);
        } else {
            billsPage = billRepo.findByBillDateBetween(start, end, pageable);
        }

        return billsPage.map(this::mapToResponseDTO);
    }

    private BillResponseDTO mapToResponseDTO(Bills bill){
        log.debug("Mapping bill to response: billId={}", bill.getBillId());
        List<BillItemResponseDTO> itemDTO=(bill.getItems() == null ? List.<BillItemModel>of() : bill.getItems()).stream().map(item ->{
            BillItemResponseDTO dto= new BillItemResponseDTO();
            dto.setBillItemId(item.getBillItemId());
            dto.setBillId(bill.getBillId());
            dto.setProductId(item.getProduct() != null ? item.getProduct().getProductId().intValue() : null);
            dto.setServiceId(item.getServices() != null ? item.getServices().getServiceId().intValue() : null);
            dto.setPackageId(item.getPackageModel() != null ? item.getPackageModel().getPackageId() : null);
            dto.setQuantity(item.getQuantity());
            dto.setSubTotal(item.getSubtotal());
            dto.setUnitPrice(item.getUnitPriceSnapshot());

            if (item.getProduct() != null) {
                dto.setProductName(item.getItemNameSnapshot());
            } else if (item.getServices() != null) {
                dto.setServiceName(item.getItemNameSnapshot());
            } else if (item.getPackageModel() != null) {
                dto.setPackageName(item.getItemNameSnapshot());
            }
            return dto;
        }).toList();

        BillResponseDTO response= new BillResponseDTO();
        response.setBillId(bill.getBillId());
        response.setBillDate(bill.getBillDate());
        response.setStaffId(Math.toIntExact(bill.getStaff().getUserId()));
        response.setStaffName(bill.getStaff().getUserName());
        response.setItems(itemDTO);
        response.setTotalAmount(bill.getBillAmount());
        response.setDues(bill.getBillDues());
        response.setDueAmount(bill.getBillDues());
        response.setCashAmount(bill.getCashAmount());
        response.setOnlineAmount(bill.getOnlineAmount());
        response.setInvoiceId(bill.getInvoiceId());
        response.setDiscountAmount(bill.getDiscountAmount());
        response.setGstPercent(bill.getGstPercent());
        response.setClientEmail(bill.getEmailId());
        response.setClientPhone(bill.getClientPhNo());
        response.setNotes(bill.getNotes());
        log.info("dues:-{}",bill.getBillDues());
        log.info("isDue:-{}",bill.getIsDue());
        if(Boolean.TRUE.equals(bill.getIsDue())) {
            response.setPaymentStatus("Due");
        } else {
            response.setPaymentStatus("Paid");
        }
        List<DuesModel> dues = duesRepo.findByBill_BillId(bill.getBillId());
        if (!dues.isEmpty()) {
            DuesModel due = dues.get(0);
            response.setDues(due.getDueAmount());
            response.setDueAmount(due.getDueAmount());
            response.setDueDate(due.getDuedate());
            response.setPaymentStatus(due.getStatus());
        }
        response.setClientName(bill.getClientName());
        response.setPaymentMode(bill.getPaymentMode());

        return response;
    }
}

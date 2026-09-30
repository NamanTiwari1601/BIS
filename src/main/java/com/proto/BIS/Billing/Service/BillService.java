package com.proto.BIS.Billing.Service;

import com.proto.BIS.Billing.DTO.BillItemRequestDTO;
import com.proto.BIS.Billing.DTO.BillItemResponseDTO;
import com.proto.BIS.Billing.DTO.BillRequestDTO;
import com.proto.BIS.Billing.DTO.BillResponseDTO;
import com.proto.BIS.Billing.Model.BillItemModel;
import com.proto.BIS.Billing.Model.Bills;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public final ServiceProductRepository serviceProductRepository;

    public final UserRepo userRepo;
    public  final DueService dueService;

    @Transactional(rollbackFor = Exception.class)
    public BillResponseDTO createBill(BillRequestDTO request){
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
        log.info("Bill Date:-{}",LocalDateTime.now());
        for (BillItemRequestDTO item : request.getItems()) {
            boolean hasProduct = item.getProductId() != null;
            boolean hasService = item.getServiceId() != null;

            BillItemModel billItem=new BillItemModel();
            if (hasProduct == hasService) {
                log.error("Each bill item must reference exactly one of product or service,not both or neither");
                throw new IllegalArgumentException("Each bill item must reference exactly one of product or service,not both or neither ");
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

                if (product.getProductQuantity() < item.getQuantity()) {
                    log.error("Insufficient stock for product: {}. Avaliable: {}", product.getProductName(), product.getProductQuantity());
                    throw new IllegalArgumentException("Insufficient stock for product: " + product.getProductName()
                            + ". Avaliable: " + product.getProductQuantity());
                }
                subtotal = product.getProductPrice() * item.getQuantity();
                log.info("Subtotal:-{}",subtotal);
                Long prevQuantity = product.getProductQuantity();
                Long newQuantity = prevQuantity - (long) item.getQuantity();
                product.setProductQuantity(newQuantity);
                productRepo.save(product);
                billItem.setProduct(product);
                billItem.setQuantity(item.getQuantity());
                log.info("Old Quantity:-{}",prevQuantity);
                log.info("New Quantity:-{}",newQuantity);
            } else {
                log.info("In Services Section");
                log.info("ServiceID:-{}",item.getServiceId());
                ServicesModel service = serviceRepo.findById(item.getServiceId())
                        .orElseThrow(() -> {
                            log.error("Services not found");
                            return new IllegalArgumentException("Services not Found");});
                subtotal = service.getServicePrice() * item.getQuantity();
                billItem.setServices(service);
                adjustLinkedProductStock(service, item.getQuantity());
                log.info("Subtotal:-{}",subtotal);
            }
            billItem.setSubtotal(subtotal);
            billItem.setBill(bills);
            billItems.add(billItem);
            total += subtotal;
        }

        log.info("Total:-{}",total);
        double dis = request.getDiscountPercent();
        log.info("Discount Percent:-{}%",dis);
        total= total-((dis/100)*total);
        double stpr = request.getGstPercent();
        log.info("GST Percent:-{}",stpr);
        total= total+((stpr/100)*total);
        log.info("Total after Discount and GST:-{}",total);

        bills.setBillAmount(total);
        bills.setDiscountPercent(request.getDiscountPercent());
        bills.setGstPercent(request.getGstPercent());
        bills.setBillDate(LocalDateTime.now());
        bills.setItems(billItems);
        if ("Due".equalsIgnoreCase(request.getPaymentStatus()) && request.getDueAmount() != null && request.getDueAmount() > 0) {
            log.info("Creating due for bill: billId={}, dueAmount={}", bills.getBillId(), request.getDueAmount());
            double amountPaid = total - request.getDueAmount();
            LocalDate dueDate = request.getDueDate() != null ? request.getDueDate() : LocalDate.now();
            dueService.createDuesFromBills(bills, amountPaid, dueDate, request.getNotes());
            bills.setBillDues(request.getDueAmount());
            bills.setIsDue(true);
        }

        billRepo.save(bills);


        return mapToResponseDTO(bills);
    }

    private void adjustLinkedProductStock(ServicesModel service, Integer serviceQuantity) {
        List<ServiceProduct> linkedProducts = serviceProductRepository
                .findByService_ServiceId(service.getServiceId());

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

            long quantityToRemove = (long) requiredQuantity * serviceQuantity;
            if (quantityToRemove < 0 || availableQuantity < quantityToRemove) {
                log.warn("Insufficient stock for product {} required by service {}. Available: {}, required: {}",
                        product.getProductName(), service.getServiceName(), availableQuantity, quantityToRemove);
                throw new IllegalArgumentException("Insufficient stock for product " + product.getProductName()
                        + " required by service " + service.getServiceName()
                        + ". Available: " + availableQuantity + ", required: " + quantityToRemove);
            }

            Long newQuantity = availableQuantity - quantityToRemove;
            product.setProductQuantity(newQuantity);
            productRepo.save(product);
            log.info("Removed {} units of product {} for service {} x{}; remaining stock: {}",
                    quantityToRemove, product.getProductName(), service.getServiceName(), serviceQuantity, newQuantity);
        }
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
        List<BillItemResponseDTO> itemDTO=bill.getItems().stream().map(item ->{
            BillItemResponseDTO dto= new BillItemResponseDTO();
            dto.setBillItemId(item.getBillItemId());
            dto.setBillId(bill.getBillId());
            dto.setQuantity(item.getQuantity());
            dto.setSubTotal(item.getSubtotal());

            if (item.getServices()!=null){
                if(item.getProduct()!=null){
                    dto.setProductName(item.getProduct().getProductName());
                }
                dto.setServiceName(item.getServices().getServiceName());
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
        log.info("dues:-{}",bill.getBillDues());
        log.info("isDue:-{}",bill.getIsDue());
        if(Boolean.TRUE.equals(bill.getIsDue())) {
            response.setPaymentStatus("Due");
        }
        response.setClientName(bill.getClientName());
        response.setPaymentMode(bill.getPaymentMode());

        return response;
    }
}

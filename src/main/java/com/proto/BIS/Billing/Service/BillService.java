package com.proto.BIS.Billing.Service;

import com.proto.BIS.Billing.DTO.BillItemRequestDTO;
import com.proto.BIS.Billing.DTO.BillItemResponseDTO;
import com.proto.BIS.Billing.DTO.BillRequestDTO;
import com.proto.BIS.Billing.DTO.BillResponseDTO;
import com.proto.BIS.Billing.Model.BillItemModel;
import com.proto.BIS.Billing.Model.Bills;
import com.proto.BIS.Billing.Repository.BillRepo;
import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Model.UserModel;
import com.proto.BIS.common.Repository.ProductRepo;
import com.proto.BIS.common.Repository.UserRepo;
import com.proto.BIS.common.Repository.WorkRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {

    public final BillRepo billRepo;

    public final ProductRepo productRepo;

    public final WorkRepo serviceRepo;

    public final UserRepo userRepo;

    @Transactional(rollbackFor = Exception.class)
    public BillResponseDTO createBill(BillRequestDTO request){
        double total=0;

            Bills bills = new Bills();
           List <BillItemModel> billItems = new ArrayList<>();
        UserModel user = userRepo.findById(request.getStaffId())
                .orElseThrow(() -> new IllegalArgumentException("Staff does not  exist")
                );
        bills.setStaff(user);
        bills.setClientName(request.getClientName());
        bills.setClientPhNo(request.getClientPhone());
        bills.setPaymentMode(request.getPaymentMode());
        bills.setEmailId(request.getEmailId());
        bills.setNotes(request.getNotes());

        for (BillItemRequestDTO item : request.getItems()) {
            boolean hasProduct = item.getProductId() != null;
            boolean hasService = item.getServiceId() != null;

            BillItemModel billItem=new BillItemModel();
            if (hasProduct == hasService) {
                throw new IllegalArgumentException("Each bill item must reference exactly one of product or service,not both or neither ");
            }
            double subtotal;
            if (hasProduct) {
                ProductModel product = productRepo.findById(item.getProductId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found"));

                if (product.getProductQuantity() < item.getQuantity()) {
                    throw new IllegalArgumentException("Insufficient stock for product: " + product.getProductName()
                            + ". Avaliable: " + product.getProductQuantity());
                }
                subtotal = product.getProductPrice() * item.getQuantity();
                Long prevQuantity = product.getProductQuantity();
                Long newQuantity = prevQuantity - (long) item.getQuantity();
                product.setProductQuantity(newQuantity);
                productRepo.save(product);
                billItem.setProduct(product);
                billItem.setQuantity(item.getQuantity());

            } else {
                ServicesModel service = serviceRepo.findById(item.getServiceId())
                        .orElseThrow(() -> new IllegalArgumentException("Services not Found"));
                subtotal = service.getServicePrice() * item.getQuantity();
                billItem.setServices(service);
            }
            billItem.setSubtotal(subtotal);
            billItem.setBill(bills);
            billItems.add(billItem);
            total += subtotal;
        }

        double dis = request.getDiscountPercent();
        total= total-((dis/100)*total);
        double stpr = request.getGstPercent();
        total= total+((stpr/100)*total);

        bills.setBillAmount(total);
        bills.setDiscountPercent(request.getDiscountPercent());
        bills.setGstPercent(request.getGstPercent());
        bills.setBillDate(LocalDateTime.now());
        bills.setItems(billItems);
        billRepo.save(bills);

        return mapToResponseDTO(bills);
    }
    public List<BillResponseDTO> getTodaysBills(){
        LocalDateTime start= LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return billRepo.findByBillDateBetween(start, end).stream()
                .map(this::mapToResponseDTO).toList();
    }
    public List<BillResponseDTO> getAllBills(){
        List<BillResponseDTO> billsList=billRepo.findAll().stream()
                .map(this::mapToResponseDTO).toList();
        return billsList;


    }
    public BillResponseDTO  getBillById(int billId){
        Bills bill= billRepo.findById(billId).orElseThrow(() -> new IllegalArgumentException("Bill not Found with id: "+billId));
        return mapToResponseDTO(bill);
    }

    public List<BillResponseDTO> getFilteredBills(Integer staffId, LocalDate from, LocalDate to){
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

    private BillResponseDTO mapToResponseDTO(Bills bill){
        List<BillItemResponseDTO> itemDTO=bill.getItems().stream().map(item ->{
            BillItemResponseDTO dto= new BillItemResponseDTO();
            dto.setBillItemId(item.getBillItemId());
            dto.setBillId(bill.getBillId());
            dto.setQuantity(item.getQuantity());
            dto.setSubTotal(item.getSubtotal());

            if(item.getProduct()!=null){
                dto.setProductName(item.getProduct().getProductName());
            }
            if (item.getServices()!=null){
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
        response.setClientName(bill.getClientName());
        response.setPaymentMode(bill.getPaymentMode());

        return response;
    }
}

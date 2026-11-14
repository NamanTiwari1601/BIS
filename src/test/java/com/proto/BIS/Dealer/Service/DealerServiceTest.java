package com.proto.BIS.Dealer.Service;

import com.proto.BIS.Dealer.DTO.DealerBillRequest;
import com.proto.BIS.Dealer.DTO.DealerPaymentRequest;
import com.proto.BIS.Dealer.Model.DealerBillModel;
import com.proto.BIS.Dealer.Model.DealerModel;
import com.proto.BIS.Dealer.Repository.DealerBillRepository;
import com.proto.BIS.Dealer.Repository.DealerDeliveryRepository;
import com.proto.BIS.Dealer.Repository.DealerRepository;
import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Repository.ProductRepo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DealerServiceTest {
    @Mock DealerRepository dealerRepository;
    @Mock DealerBillRepository billRepository;
    @Mock DealerDeliveryRepository deliveryRepository;
    @Mock ProductRepo productRepository;
    @InjectMocks DealerService service;

    @Test
    void createsBillFromValidatedProductLineItems() {
        DealerModel dealer = DealerModel.builder().dealerId(4L).name("Supply Co").phone("1234567890")
                .products(new ArrayList<>()).build();
        ProductModel product = new ProductModel();
        product.setProductId(7L);
        product.setProductName("Shampoo");
        when(dealerRepository.findById(4L)).thenReturn(Optional.of(dealer));
        when(productRepository.findAllByProductIdIn(List.of(7L))).thenReturn(List.of(product));
        when(billRepository.save(any(DealerBillModel.class))).thenAnswer(invocation -> {
            DealerBillModel bill = invocation.getArgument(0);
            bill.setBillId(9L);
            return bill;
        });

        var response = service.createBill(4L, billRequest("20.00", "5.00"));

        assertEquals(9L, response.getBillId());
        assertEquals(new BigDecimal("20.00"), response.getTotalAmount());
        assertEquals(new BigDecimal("5.00"), response.getPaidAmount());
        assertEquals(new BigDecimal("15.00"), response.getDueAmount());
        assertEquals("Shampoo", response.getItems().get(0).getProductName());
        assertEquals(new BigDecimal("5.00"), response.getPayments().get(0).getAmount());
        verify(billRepository).save(any(DealerBillModel.class));
    }

    @Test
    void rejectsBillTotalThatDoesNotMatchLineItems() {
        when(dealerRepository.findById(4L)).thenReturn(Optional.of(
                DealerModel.builder().dealerId(4L).products(new ArrayList<>()).build()));
        when(productRepository.findAllByProductIdIn(List.of(7L))).thenReturn(List.of(new ProductModel()));

        assertThrows(IllegalArgumentException.class,
                () -> service.createBill(4L, billRequest("19.99", "0.00")));
        verify(billRepository, never()).save(any());
    }

    @Test
    void rejectsPaymentLargerThanOutstandingBalance() {
        DealerBillModel bill = billWithBalance("100.00", "70.00", "30.00");
        when(billRepository.findByIdAndDealer_DealerIdForUpdate(9L, 4L)).thenReturn(Optional.of(bill));
        DealerPaymentRequest request = paymentRequest("30.01");

        assertThrows(IllegalArgumentException.class, () -> service.addPayment(4L, 9L, request));
        assertEquals(new BigDecimal("70.00"), bill.getPaidAmount());
        verify(billRepository, never()).save(bill);
    }

    @Test
    void recordsPaymentAndRecalculatesOutstandingBalance() {
        DealerBillModel bill = billWithBalance("100.00", "70.00", "30.00");
        when(billRepository.findByIdAndDealer_DealerIdForUpdate(9L, 4L)).thenReturn(Optional.of(bill));

        var response = service.addPayment(4L, 9L, paymentRequest("12.50"));

        assertEquals(new BigDecimal("82.50"), response.getPaidAmount());
        assertEquals(new BigDecimal("17.50"), response.getDueAmount());
        assertEquals(1, response.getPayments().size());
        assertEquals("CASH", response.getPayments().get(0).getMethod());
        verify(billRepository).save(bill);
    }

    @Test
    void rejectsPaymentForBillOwnedByAnotherDealer() {
        when(billRepository.findByIdAndDealer_DealerIdForUpdate(9L, 4L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.addPayment(4L, 9L, paymentRequest("1.00")));
    }

    private DealerBillRequest billRequest(String total, String paid) {
        DealerBillRequest request = new DealerBillRequest();
        request.setInvoiceNumber("INV-1");
        request.setBillDate(LocalDate.of(2026, 11, 10));
        request.setTotalAmount(new BigDecimal(total));
        request.setPaidAmount(new BigDecimal(paid));
        DealerBillRequest.ItemRequest item = new DealerBillRequest.ItemRequest();
        item.setProductId(7L);
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("10.00"));
        request.setItems(List.of(item));
        return request;
    }

    private DealerPaymentRequest paymentRequest(String amount) {
        DealerPaymentRequest request = new DealerPaymentRequest();
        request.setAmount(new BigDecimal(amount));
        request.setPaymentDate(LocalDate.of(2026, 11, 11));
        request.setMethod("Cash");
        return request;
    }

    private DealerBillModel billWithBalance(String total, String paid, String due) {
        return DealerBillModel.builder()
                .billId(9L)
                .dealer(DealerModel.builder().dealerId(4L).build())
                .invoiceNumber("INV-1")
                .billDate(LocalDate.of(2026, 11, 10))
                .totalAmount(new BigDecimal(total))
                .paidAmount(new BigDecimal(paid))
                .dueAmount(new BigDecimal(due))
                .items(new ArrayList<>())
                .payments(new ArrayList<>())
                .build();
    }
}

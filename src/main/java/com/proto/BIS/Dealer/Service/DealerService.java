package com.proto.BIS.Dealer.Service;

import com.proto.BIS.Dealer.DTO.DealerBillRequest;
import com.proto.BIS.Dealer.DTO.DealerDeliveryRequest;
import com.proto.BIS.Dealer.DTO.DealerPaymentRequest;
import com.proto.BIS.Dealer.DTO.DealerRequest;
import com.proto.BIS.Dealer.DTO.DealerResponse;
import com.proto.BIS.Dealer.Model.DealerBillItemModel;
import com.proto.BIS.Dealer.Model.DealerBillModel;
import com.proto.BIS.Dealer.Model.DealerDeliveryModel;
import com.proto.BIS.Dealer.Model.DealerModel;
import com.proto.BIS.Dealer.Model.DealerPaymentModel;
import com.proto.BIS.Dealer.Repository.DealerBillRepository;
import com.proto.BIS.Dealer.Repository.DealerDeliveryRepository;
import com.proto.BIS.Dealer.Repository.DealerRepository;
import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Repository.ProductRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DealerService {
    private static final BigDecimal ZERO = new BigDecimal("0.00");
    private static final Set<String> PAYMENT_METHODS =
            Set.of("CASH", "ONLINE", "BANK TRANSFER", "CHEQUE", "OTHER");
    private static final Set<String> DELIVERY_STATUSES =
            Set.of("DELIVERED", "PARTIAL", "PENDING");

    private final DealerRepository dealerRepository;
    private final DealerBillRepository billRepository;
    private final DealerDeliveryRepository deliveryRepository;
    private final ProductRepo productRepository;

    @Transactional(readOnly = true)
    public Page<DealerResponse> search(String query, Pageable pageable) {
        String pattern = query == null || query.isBlank()
                ? "%"
                : "%" + query.trim().toLowerCase() + "%";
        Page<DealerModel> dealers = dealerRepository.search(pattern, pageable);
        List<Long> dealerIds = dealers.getContent().stream().map(DealerModel::getDealerId).toList();
        Map<Long, BigDecimal> dues = new HashMap<>();
        if (!dealerIds.isEmpty()) {
            billRepository.sumDuesByDealer_DealerIds(dealerIds).forEach(
                    summary -> dues.put(summary.getDealerId(), money(summary.getTotalDue())));
        }
        return dealers.map(dealer -> toResponse(dealer, dues.getOrDefault(dealer.getDealerId(), ZERO),
                List.of(), List.of()));
    }
    @Transactional(readOnly = true)
    public DealerResponse get(Long dealerId) {
        DealerModel dealer = findDealer(dealerId);
        List<DealerBillModel> bills = billRepository
                .findByDealer_DealerIdOrderByBillDateDescBillIdDesc(dealerId);
        List<DealerDeliveryModel> deliveries = deliveryRepository
                .findAllByDealer_DealerIdOrderByDeliveryDateDescDeliveryIdDesc(dealerId);
        BigDecimal totalDue = bills.stream().map(DealerBillModel::getDueAmount)
                .map(DealerService::money).reduce(ZERO, BigDecimal::add);
        return toResponse(dealer, totalDue, bills, deliveries);
    }

    public DealerResponse create(DealerRequest request) {
        DealerModel dealer = new DealerModel();
        applyDealerRequest(dealer, request);
        return toResponse(dealerRepository.save(dealer), ZERO, List.of(), List.of());
    }

    public DealerResponse update(Long dealerId, DealerRequest request) {
        DealerModel dealer = findDealer(dealerId);
        applyDealerRequest(dealer, request);
        return get(dealerRepository.save(dealer).getDealerId());
    }

    public DealerResponse.BillResponse createBill(Long dealerId, DealerBillRequest request) {
        DealerModel dealer = findDealer(dealerId);
        String invoiceNumber = request.getInvoiceNumber().trim();
        if (billRepository.existsByDealer_DealerIdAndInvoiceNumberIgnoreCase(dealerId, invoiceNumber)) {
            throw new IllegalArgumentException("Invoice number already exists for this dealer");
        }

        BigDecimal calculatedTotal = ZERO;
        List<DealerBillItemModel> items = new ArrayList<>();
        for (DealerBillRequest.ItemRequest itemRequest : request.getItems()) {
            ProductModel product = findProduct(itemRequest.getProductId());
            BigDecimal unitPrice = money(itemRequest.getUnitPrice());
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            calculatedTotal = calculatedTotal.add(subtotal);
            items.add(DealerBillItemModel.builder()
                    .product(product)
                    .productName(product.getProductName())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .subtotal(subtotal)
                    .build());
        }
        calculatedTotal = money(calculatedTotal);
        if (calculatedTotal.compareTo(money(request.getTotalAmount())) != 0) {
            throw new IllegalArgumentException("Bill total must equal the sum of its product line items");
        }

        BigDecimal paidAmount = request.getPaidAmount() == null ? ZERO : money(request.getPaidAmount());
        if (paidAmount.compareTo(calculatedTotal) > 0) {
            throw new IllegalArgumentException("Paid amount cannot exceed the bill total");
        }
        DealerBillModel bill = DealerBillModel.builder()
                .dealer(dealer)
                .invoiceNumber(invoiceNumber)
                .billDate(request.getBillDate())
                .totalAmount(calculatedTotal)
                .paidAmount(paidAmount)
                .dueAmount(calculatedTotal.subtract(paidAmount))
                .notes(blankToNull(request.getNotes()))
                .items(items)
                .payments(new ArrayList<>())
                .build();
        items.forEach(item -> item.setBill(bill));
        if (paidAmount.compareTo(ZERO) > 0) {
            bill.getPayments().add(DealerPaymentModel.builder()
                    .bill(bill)
                    .amount(paidAmount)
                    .paymentDate(bill.getBillDate())
                    .method("OTHER")
                    .notes("Initial payment recorded with purchase bill")
                    .build());
        }
        DealerBillModel saved = billRepository.save(bill);
        return toBillResponse(saved);
    }

    public DealerResponse.BillResponse addPayment(
            Long dealerId, Long billId, DealerPaymentRequest request) {
        DealerBillModel bill = findBillForUpdate(dealerId, billId);
        BigDecimal amount = money(request.getAmount());
        if (amount.compareTo(bill.getDueAmount()) > 0) {
            throw new IllegalArgumentException("Payment cannot exceed the outstanding balance");
        }
        String method = normalizePaymentMethod(request.getMethod());
        DealerPaymentModel payment = DealerPaymentModel.builder()
                .bill(bill)
                .amount(amount)
                .paymentDate(request.getPaymentDate())
                .method(method)
                .notes(blankToNull(request.getNotes()))
                .build();
        bill.getPayments().add(payment);
        bill.setPaidAmount(bill.getPaidAmount().add(amount));
        bill.setDueAmount(bill.getTotalAmount().subtract(bill.getPaidAmount()));
        billRepository.save(bill);
        return toBillResponse(bill);
    }

    public DealerResponse.DeliveryResponse addDelivery(Long dealerId, DealerDeliveryRequest request) {
        DealerModel dealer = findDealer(dealerId);
        DealerBillModel bill = null;
        if (request.getBillId() != null) {
            bill = billRepository.findByBillIdAndDealerId(request.getBillId(), dealerId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Purchase bill not found for dealer: " + request.getBillId()));
        }
        DealerDeliveryModel delivery = DealerDeliveryModel.builder()
                .dealer(dealer)
                .bill(bill)
                .deliveryDate(request.getDeliveryDate())
                .status(normalizeDeliveryStatus(request.getStatus()))
                .notes(blankToNull(request.getNotes()))
                .build();
        return toDeliveryResponse(deliveryRepository.save(delivery));
    }

    private void applyDealerRequest(DealerModel dealer, DealerRequest request) {
        dealer.setName(request.getName().trim());
        dealer.setPhone(request.getPhone().trim());
        Collection<Long> productIds = request.getProductIds();
        if (productIds.stream().anyMatch(id -> id == null || id < 1)) {
            throw new IllegalArgumentException("Product IDs must be positive values");
        }
        if (new HashSet<>(productIds).size() != productIds.size()) {
            throw new IllegalArgumentException("Product IDs must not contain duplicates");
        }
        List<ProductModel> products = productRepository.findAllByProductIdIn(productIds);
        if (products.size() != productIds.size()) {
            Set<Long> foundIds = products.stream().map(ProductModel::getProductId).collect(Collectors.toSet());
            List<Long> missingIds = productIds.stream()
                    .filter(id -> !foundIds.contains(id)).toList();
            throw new EntityNotFoundException("Product not found: " + missingIds);
        }
        dealer.setProducts(products);
    }

    private DealerModel findDealer(Long dealerId) {
        return dealerRepository.findById(dealerId)
                .orElseThrow(() -> new EntityNotFoundException("Dealer not found: " + dealerId));
    }

    private ProductModel findProduct(Long productId) {
        return productRepository.findAllByProductIdIn(List.of(productId)).stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));
    }

    private DealerBillModel findBillForUpdate(Long dealerId, Long billId) {
        return billRepository.findByIdAndDealer_DealerIdForUpdate(billId, dealerId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Purchase bill not found for dealer: " + billId));
    }

    private DealerResponse toResponse(
            DealerModel dealer,
            BigDecimal totalDue,
            List<DealerBillModel> bills,
            List<DealerDeliveryModel> deliveries) {
        return DealerResponse.builder()
                .dealerId(dealer.getDealerId())
                .name(dealer.getName())
                .phone(dealer.getPhone())
                .totalDue(money(totalDue))
                .products(dealer.getProducts().stream().map(product ->
                        DealerResponse.ProductResponse.builder()
                                .productId(product.getProductId().longValue())
                                .productName(product.getProductName())
                                .build()).toList())
                .bills(bills.stream().map(this::toBillResponse).toList())
                .deliveries(deliveries.stream().map(this::toDeliveryResponse).toList())
                .build();
    }

    private DealerResponse.BillResponse toBillResponse(DealerBillModel bill) {
        return DealerResponse.BillResponse.builder()
                .billId(bill.getBillId())
                .invoiceNumber(bill.getInvoiceNumber())
                .billDate(bill.getBillDate())
                .totalAmount(money(bill.getTotalAmount()))
                .paidAmount(money(bill.getPaidAmount()))
                .dueAmount(money(bill.getDueAmount()))
                .notes(bill.getNotes())
                .items(bill.getItems().stream().map(item ->
                        DealerResponse.BillItemResponse.builder()
                                .billItemId(item.getBillItemId())
                                .productId(item.getProduct().getProductId().longValue())
                                .productName(item.getProductName())
                                .quantity(item.getQuantity())
                                .unitPrice(money(item.getUnitPrice()))
                                .subtotal(money(item.getSubtotal()))
                                .build()).toList())
                .payments(bill.getPayments().stream().map(payment ->
                        DealerResponse.PaymentResponse.builder()
                                .paymentId(payment.getPaymentId())
                                .amount(money(payment.getAmount()))
                                .paymentDate(payment.getPaymentDate())
                                .method(payment.getMethod())
                                .notes(payment.getNotes())
                                .build()).toList())
                .build();
    }

    private DealerResponse.DeliveryResponse toDeliveryResponse(DealerDeliveryModel delivery) {
        DealerBillModel bill = delivery.getBill();
        return DealerResponse.DeliveryResponse.builder()
                .deliveryId(delivery.getDeliveryId())
                .billId(bill == null ? null : bill.getBillId())
                .invoiceNumber(bill == null ? null : bill.getInvoiceNumber())
                .deliveryDate(delivery.getDeliveryDate())
                .status(toDisplayStatus(delivery.getStatus()))
                .notes(delivery.getNotes())
                .build();
    }

    private String normalizePaymentMethod(String method) {
        String normalized = method.trim().toUpperCase(Locale.ROOT);
        if (!PAYMENT_METHODS.contains(normalized)) {
            throw new IllegalArgumentException("Unsupported payment method");
        }
        return normalized;
    }

    private String normalizeDeliveryStatus(String status) {
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (!DELIVERY_STATUSES.contains(normalized)) {
            throw new IllegalArgumentException("Delivery status must be Delivered, Partial, or Pending");
        }
        return normalized;
    }

    private String toDisplayStatus(String status) {
        String normalized = status.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static BigDecimal money(BigDecimal value) {
        return value == null ? ZERO : value.setScale(2, java.math.RoundingMode.UNNECESSARY);
    }
}

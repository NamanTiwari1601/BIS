package com.proto.BIS.Dealer.Controller;

import com.proto.BIS.Dealer.DTO.DealerBillRequest;
import com.proto.BIS.Dealer.DTO.DealerDeliveryRequest;
import com.proto.BIS.Dealer.DTO.DealerPaymentRequest;
import com.proto.BIS.Dealer.DTO.DealerRequest;
import com.proto.BIS.Dealer.DTO.DealerResponse;
import com.proto.BIS.Dealer.Service.DealerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dealer")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Validated
public class DealerController {
    private final DealerService dealerService;

    @GetMapping("/page")
    public Page<DealerResponse> search(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return dealerService.search(q, PageRequest.of(page, size));
    }

    @GetMapping("/{dealerId}")
    public DealerResponse get(@PathVariable @Min(1) Long dealerId) {
        return dealerService.get(dealerId);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public DealerResponse create(@Valid @RequestBody DealerRequest request) {
        return dealerService.create(request);
    }

    @PutMapping("/{dealerId}")
    public DealerResponse update(
            @PathVariable @Min(1) Long dealerId,
            @Valid @RequestBody DealerRequest request) {
        return dealerService.update(dealerId, request);
    }

    @PostMapping("/{dealerId}/bills")
    @ResponseStatus(HttpStatus.CREATED)
    public DealerResponse.BillResponse createBill(
            @PathVariable @Min(1) Long dealerId,
            @Valid @RequestBody DealerBillRequest request) {
        return dealerService.createBill(dealerId, request);
    }

    @PostMapping("/{dealerId}/bills/{billId}/payments")
    public DealerResponse.BillResponse addPayment(
            @PathVariable @Min(1) Long dealerId,
            @PathVariable @Min(1) Long billId,
            @Valid @RequestBody DealerPaymentRequest request) {
        return dealerService.addPayment(dealerId, billId, request);
    }

    @PostMapping("/{dealerId}/deliveries")
    @ResponseStatus(HttpStatus.CREATED)
    public DealerResponse.DeliveryResponse addDelivery(
            @PathVariable @Min(1) Long dealerId,
            @Valid @RequestBody DealerDeliveryRequest request) {
        return dealerService.addDelivery(dealerId, request);
    }
}

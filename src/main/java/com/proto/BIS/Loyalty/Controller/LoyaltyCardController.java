package com.proto.BIS.Loyalty.Controller;

import com.proto.BIS.Loyalty.DTO.LoyaltyCardRequest;
import com.proto.BIS.Loyalty.DTO.LoyaltyCardResponse;
import com.proto.BIS.Loyalty.Service.LoyaltyCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loyalty/cards")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class LoyaltyCardController {
    private final LoyaltyCardService loyaltyCardService;

    @GetMapping
    public List<LoyaltyCardResponse> getAll() {
        return loyaltyCardService.getAll();
    }

    @PostMapping
    public ResponseEntity<LoyaltyCardResponse> create(@Valid @RequestBody LoyaltyCardRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loyaltyCardService.create(request));
    }

    @PutMapping("/{id}")
    public LoyaltyCardResponse update(@PathVariable Long id, @Valid @RequestBody LoyaltyCardRequest request) {
        return loyaltyCardService.update(id, request);
    }

    @PostMapping("/{id}/paid-visits")
    public LoyaltyCardResponse recordPaidVisit(@PathVariable Long id) {
        return loyaltyCardService.recordPaidVisit(id);
    }

    @PostMapping("/{id}/free-visit")
    public LoyaltyCardResponse recordFreeVisit(@PathVariable Long id) {
        return loyaltyCardService.recordFreeVisit(id);
    }
}

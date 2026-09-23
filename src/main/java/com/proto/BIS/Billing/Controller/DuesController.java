package com.proto.BIS.Billing.Controller;

import com.proto.BIS.Billing.Model.DuesModel;
import com.proto.BIS.Billing.Service.DueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dues")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
@Tag(name = "Dues", description = "Manage customer dues")
public class DuesController {

    private final DueService service;

    @GetMapping
    @Operation(summary = "Get all dues", description = "Returns the list of pending and completed dues")
    public ResponseEntity<List<DuesModel>> getAllDues() {
        return ResponseEntity.ok(service.getAllDues());
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending dues", description = "Returns all unpaid or partially paid dues")
    public ResponseEntity<List<DuesModel>> getPendingDues() {
        return ResponseEntity.ok(service.getPendingDues());
    }

    @PostMapping
    @Operation(summary = "Create standalone dues", description = "Creates a dues record independent of a bill")
    public ResponseEntity<?> createStandaloneDues(@RequestBody DuesModel dues) {
        try {
            DuesModel saved = service.createStandaloneDues(dues);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

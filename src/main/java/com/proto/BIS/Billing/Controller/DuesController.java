package com.proto.BIS.Billing.Controller;

import com.proto.BIS.Billing.Model.DuesModel;
import com.proto.BIS.Billing.Service.DueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final Logger log= LoggerFactory.getLogger(DuesController.class);

    @GetMapping
    @Operation(summary = "Get all dues", description = "Returns the list of pending and completed dues")
    public ResponseEntity<List<DuesModel>> getAllDues() {
        log.info("In get all dues");
        return ResponseEntity.ok(service.getAllDues());
    }

    @GetMapping("/page")
    @Operation(summary = "Get paginated dues", description = "Returns a page of dues records")
    public ResponseEntity<Page<DuesModel>> getDuesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("In Dues Page");
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getAllDues(pageable));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get pending dues", description = "Returns all unpaid or partially paid dues")
    public ResponseEntity<List<DuesModel>> getPendingDues() {
        log.info("In get Pending Dues");
        return ResponseEntity.ok(service.getPendingDues());
    }

    @PostMapping
    @Operation(summary = "Create standalone dues", description = "Creates a dues record independent of a bill")
    public ResponseEntity<?> createStandaloneDues(@RequestBody DuesModel dues) {
        log.info("In Create Standalone Dues");
        try {
            DuesModel saved = service.createStandaloneDues(dues);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

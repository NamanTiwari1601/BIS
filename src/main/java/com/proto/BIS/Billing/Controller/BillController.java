package com.proto.BIS.Billing.Controller;

import com.proto.BIS.Billing.DTO.BillRequestDTO;
import com.proto.BIS.Billing.DTO.BillResponseDTO;
import com.proto.BIS.Billing.Service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/bill")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
@Tag(name="Bill", description = "Managing bills")
public class BillController {

    public final BillService service;
    private static final Logger log= LoggerFactory.getLogger(BillController.class);

    @PostMapping("/create")
    @Operation(summary = "create Bills", description="create bills by this")
    public ResponseEntity<?> createBill(@RequestBody BillRequestDTO bill, Principal principal){
        log.info("In create bill");
            BillResponseDTO save=service.createBill(bill, principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(save);

    }

    @GetMapping("/All")
    @Operation(summary = "get all bills",description="Returns list of all bills")
    public ResponseEntity<List<BillResponseDTO>> getAllBills(){
        log.info("In get All Bills");
        return ResponseEntity.ok(service.getAllBills());
    }

    @GetMapping("/today")
    @Operation(summary = "get today's bills", description = "Returns a list  of  today's  bills")
    public  ResponseEntity<List<BillResponseDTO>> getTodayBills(){
        log.info("In get today's bills");
        return ResponseEntity.ok(service.getTodaysBills());
    }
    @GetMapping("/{billsId}")
    @Operation(summary = "get bill by id", description = "returns bill by Id")
    public ResponseEntity<BillResponseDTO> getBillsById (@PathVariable int billsId){
        log.info("In get bill By id");
        return ResponseEntity.ok(service.getBillById(billsId));
    }

    @PutMapping("/{billId}")
    @Operation(summary = "Update a bill", description = "Updates a bill and reconciles its stock and dues")
    public ResponseEntity<BillResponseDTO> updateBill(
            @PathVariable int billId,
            @RequestBody BillRequestDTO bill,
            Principal principal) {
        return ResponseEntity.ok(service.updateBill(billId, bill, principal.getName()));
    }

    @DeleteMapping("/{billId}")
    @Operation(summary = "Delete a bill", description = "Deletes a bill with optional stock and dues restoration")
    public ResponseEntity<Void> deleteBill(
            @PathVariable int billId,
            @RequestParam boolean restoreStock,
            @RequestParam boolean restoreDues,
            Principal principal) {
        service.deleteBill(billId, restoreStock, restoreDues, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/history")
    @Operation(summary = "get bills history", description="Returns a paginated list of bills for history page")
    public ResponseEntity<Page<BillResponseDTO>> getBillsHistory(
            @RequestParam(required = false) Integer staffId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        log.info("In Get Bills History");
        log.info("From Date:-{}", from);
        log.info("toDate:-{}", to);
        LocalDate fromDate = from != null ? LocalDate.parse(from) : null;
        LocalDate toDate = to != null ? LocalDate.parse(to) : null;
        Pageable pageable = PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"billDate"));
        return ResponseEntity.ok(service.getFilteredBills(staffId, fromDate, toDate,pageable));
    }



}

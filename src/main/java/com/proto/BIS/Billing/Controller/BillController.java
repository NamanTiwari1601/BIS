package com.proto.BIS.Billing.Controller;

import com.proto.BIS.Billing.DTO.BillItemResponseDTO;
import com.proto.BIS.Billing.DTO.BillRequestDTO;
import com.proto.BIS.Billing.DTO.BillResponseDTO;
import com.proto.BIS.Billing.Model.Bills;
import com.proto.BIS.Billing.Service.BillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/bill")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
@Tag(name="Bill", description = "Managing bills")
public class BillController {

    public final BillService service;

    @PostMapping("/create")
    @Operation(summary = "create Bills", description="create bills by this")
    public ResponseEntity<?> createBill(@RequestBody BillRequestDTO bill){
            BillResponseDTO save=service.createBill(bill);
            return ResponseEntity.status(HttpStatus.CREATED).body(save);

    }

    @GetMapping("/All")
    @Operation(summary = "get all bills",description="Returns list of all bills")
    public ResponseEntity<List<BillResponseDTO>> getAllBills(){
        return ResponseEntity.ok(service.getAllBills());
    }

    @GetMapping("/today")
    @Operation(summary = "get today's bills", description = "Returns a list  of  today's  bills")
    public  ResponseEntity<List<BillResponseDTO>> getTodayBills(){
        return ResponseEntity.ok(service.getTodaysBills());
    }
    @GetMapping("/{billsId}")
    @Operation(summary = "get bill by id", description = "returns bill by Id")
    public ResponseEntity<BillResponseDTO> getBillsById (@PathVariable int billsId){
        return ResponseEntity.ok(service.getBillById(billsId));
    }

    @GetMapping("/history")
    @Operation(summary = "get bills history", description="Returns a list  of  bills for history page ")
    public ResponseEntity<List<BillResponseDTO>> getBillsHistory(
            @RequestParam(required = false) Integer staffId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ){
        LocalDate fromDate= from != null ? LocalDate.parse(from): null;
        LocalDate toDate= to != null ? LocalDate.parse(to): null;
        return ResponseEntity.ok(service.getFilteredBills(staffId,fromDate,toDate));


    }



}

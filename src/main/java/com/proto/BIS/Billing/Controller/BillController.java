package com.proto.BIS.Billing.Controller;

import com.proto.BIS.Billing.DTO.BillItemResponseDTO;
import com.proto.BIS.Billing.DTO.BillRequestDTO;
import com.proto.BIS.Billing.DTO.BillResponseDTO;
import com.proto.BIS.Billing.Model.Bills;
import com.proto.BIS.Billing.Service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    public final BillService service;

    @PostMapping("/create")
    public ResponseEntity<?> createBill(@RequestBody BillRequestDTO bill){
        try{
            BillResponseDTO save=service.createBill(bill);
            return ResponseEntity.status(HttpStatus.CREATED).body(save);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/All")
    public ResponseEntity<List<BillResponseDTO>> getAllBills(){
        return ResponseEntity.ok(service.getAllBills());
    }

    @GetMapping("/today")
    public  ResponseEntity<List<BillResponseDTO>> getTodayBills(){
        return ResponseEntity.ok(service.getTodaysBills());
    }
    @GetMapping("/{billsId}")
    public ResponseEntity<BillResponseDTO> getBillsById (@PathVariable int billsId){
        return ResponseEntity.ok(service.getBillById(billsId));
    }



}

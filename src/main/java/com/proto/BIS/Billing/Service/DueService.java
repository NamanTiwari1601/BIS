package com.proto.BIS.Billing.Service;

import com.proto.BIS.Billing.Model.Bills;
import com.proto.BIS.Billing.Model.DuesModel;
import com.proto.BIS.Billing.Repository.DuesRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DueService {
    public final DuesRepo repo ;
    public DuesModel createDuesFromBills(Bills bill, double amountPaid, LocalDate dueDate,String notes){


        DuesModel dues = new DuesModel();
        double totalAmount=bill.getBillAmount();
        double duesAmount= bill.getBillDues();
        double paidAmount= totalAmount-duesAmount;
        dues.setDueAmount(bill.getBillDues());
        dues.setClientName(bill.getClientName());
        dues.setClientPhone(bill.getClientPhNo());
        dues.setDuedate(dueDate);
        dues.setBill(bill);
        dues.setTotalAmount(bill.getBillAmount());
        dues.setPaidAmount(paidAmount);

        if(paidAmount==0.0)
            dues.setStatus("PENDING");
        if(paidAmount>0.0)
            dues.setStatus("PARTIAL");

        dues.setNotes(notes);
        repo.save(dues);

        return dues;
    }


}

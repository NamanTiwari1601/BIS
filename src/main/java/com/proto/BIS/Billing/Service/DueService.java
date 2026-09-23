package com.proto.BIS.Billing.Service;

import com.proto.BIS.Billing.Model.Bills;
import com.proto.BIS.Billing.Model.DuesModel;
import com.proto.BIS.Billing.Repository.DuesRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DueService {
    private final DuesRepo repo;

    public List<DuesModel> getAllDues() {
        return repo.findAll();
    }

    public List<DuesModel> getPendingDues() {
        return repo.findByStatusIn(List.of("PENDING", "PARTIAL"));
    }

    public DuesModel createStandaloneDues(DuesModel dues) {
        if (dues == null) {
            throw new IllegalArgumentException("Dues payload is required");
        }
        if (dues.getClientName() == null || dues.getClientName().isBlank()) {
            throw new IllegalArgumentException("Client name is required");
        }
        if (dues.getClientPhone() == null || dues.getClientPhone().isBlank()) {
            throw new IllegalArgumentException("Client phone is required");
        }

        double totalAmount = Math.max(0.0, dues.getTotalAmount());
        double paidAmount = Math.max(0.0, dues.getPaidAmount());
        double dueAmount = Math.max(0.0, totalAmount - paidAmount);

        dues.setTotalAmount(totalAmount);
        dues.setPaidAmount(paidAmount);
        dues.setDueAmount(dueAmount);
        dues.setDuedate(dues.getDuedate() != null ? dues.getDuedate() : LocalDate.now());

        if (dueAmount <= 0.0) {
            dues.setStatus("PAID");
        } else if (paidAmount <= 0.0) {
            dues.setStatus("PENDING");
        } else {
            dues.setStatus("PARTIAL");
        }

        if (dues.getNotes() == null) {
            dues.setNotes("");
        }

        return repo.save(dues);
    }

    public DuesModel createDuesFromBills(Bills bill, double amountPaid, LocalDate dueDate, String notes) {
        double totalAmount = bill.getBillAmount();
        double dueAmount = Math.max(0.0, totalAmount - amountPaid);
        double paidAmount = Math.min(totalAmount, amountPaid);

        DuesModel dues = new DuesModel();
        dues.setDueAmount(dueAmount);
        dues.setClientName(bill.getClientName());
        dues.setClientPhone(bill.getClientPhNo());
        dues.setDuedate(dueDate);
        dues.setBill(bill);
        dues.setTotalAmount(totalAmount);
        dues.setPaidAmount(paidAmount);
        dues.setNotes(notes);

        if (dueAmount <= 0.0) {
            dues.setStatus("PAID");
        } else if (paidAmount <= 0.0) {
            dues.setStatus("PENDING");
        } else {
            dues.setStatus("PARTIAL");
        }

        repo.save(dues);
        return dues;
    }
}

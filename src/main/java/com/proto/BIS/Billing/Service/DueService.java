package com.proto.BIS.Billing.Service;

import com.proto.BIS.Billing.Model.Bills;
import com.proto.BIS.Billing.Model.DuesModel;
import com.proto.BIS.Billing.Repository.DuesRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DueService {
    private final DuesRepo repo;

    public List<DuesModel> getAllDues() {
        log.info("In get all dues");
        return repo.findAll();
    }

    public Page<DuesModel> getAllDues(Pageable pageable) {
        log.info("In get paginated dues: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repo.findAll(pageable);
    }

    public List<DuesModel> getPendingDues() {
        log.info("In get pending dues");
        return repo.findByStatusIn(List.of("PENDING", "PARTIAL"));
    }

    public DuesModel createStandaloneDues(DuesModel dues) {
        log.info("In create standalone dues");
        if (dues == null) {
            log.warn("Dues payload is null");
            throw new IllegalArgumentException("Dues payload is required");
        }
        if (dues.getClientName() == null || dues.getClientName().isBlank()) {
            log.warn("Dues client name is missing");
            throw new IllegalArgumentException("Client name is required");
        }
        if (dues.getClientPhone() == null || dues.getClientPhone().isBlank()) {
            log.warn("Dues client phone is missing");
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
        log.info("In create dues from bill: billId={}, amountPaid={}", bill != null ? bill.getBillId() : null, amountPaid);
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

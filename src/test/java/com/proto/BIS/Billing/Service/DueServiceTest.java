package com.proto.BIS.Billing.Service;

import com.proto.BIS.Billing.Model.DuesModel;
import com.proto.BIS.Billing.Repository.DuesRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DueServiceTest {

    @Mock
    private DuesRepo duesRepo;

    @InjectMocks
    private DueService dueService;

    @Test
    void shouldReturnPendingAndPartialDuesOnly() {
        DuesModel pending = new DuesModel();
        pending.setStatus("PENDING");

        DuesModel partial = new DuesModel();
        partial.setStatus("PARTIAL");

        DuesModel paid = new DuesModel();
        paid.setStatus("PAID");

        when(duesRepo.findByStatusIn(List.of("PENDING", "PARTIAL"))).thenReturn(List.of(pending, partial));

        List<DuesModel> result = dueService.getPendingDues();

        assertEquals(2, result.size());
    }
}

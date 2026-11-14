package com.proto.BIS.Dealer.Repository;

import com.proto.BIS.Dealer.Model.DealerBillModel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DealerBillRepository extends JpaRepository<DealerBillModel, Long> {
    List<DealerBillModel> findByDealer_DealerIdOrderByBillDateDescBillIdDesc(Long dealerId);

    @Query("SELECT b FROM DealerBillModel b WHERE b.billId = :billId AND b.dealer.dealerId = :dealerId")
    Optional<DealerBillModel> findByBillIdAndDealerId(@Param("billId") Long billId,
                                                      @Param("dealerId") Long dealerId);

    boolean existsByDealer_DealerIdAndInvoiceNumberIgnoreCase(Long dealerId, String invoiceNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM DealerBillModel b WHERE b.billId = :billId AND b.dealer.dealerId = :dealerId")
    Optional<DealerBillModel> findByIdAndDealer_DealerIdForUpdate(
            @Param("billId") Long billId,
            @Param("dealerId") Long dealerId);

    @Query("SELECT b.dealer.dealerId AS dealerId, SUM(b.dueAmount) AS totalDue "
            + "FROM DealerBillModel b WHERE b.dealer.dealerId IN :dealerIds GROUP BY b.dealer.dealerId")
    List<DealerDueSummary> sumDuesByDealer_DealerIds(@Param("dealerIds") Collection<Long> dealerIds);

    interface DealerDueSummary {
        Long getDealerId();
        java.math.BigDecimal getTotalDue();
    }
}

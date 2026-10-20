package com.proto.BIS.Billing.Repository;

import com.proto.BIS.Billing.Model.Bills;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BillRepo extends JpaRepository<Bills,Integer> {
    @Query("SELECT b FROM Bills b WHERE b.billDate BETWEEN :start AND :end")
    List<Bills> findByBillDateBetween(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    @Query("SELECT b FROM Bills b WHERE b.billDate BETWEEN :start AND :end")
    Page<Bills> findByBillDateBetween(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      Pageable pageable);

    List<Bills> findByStaffUserId(Integer staffId);
    Page<Bills> findByStaffUserId(Integer staffId, Pageable pageable);

    @Query("SELECT b FROM Bills b WHERE b.staff.userId = :staffId AND b.billDate BETWEEN :start AND :end")
    List<Bills> findByStaffUserIdAndBillDateBetween(@Param("staffId") Integer staffId,
                                                    @Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end);

    @Query("SELECT b FROM Bills b WHERE b.staff.userId = :staffId AND b.billDate BETWEEN :start AND :end")
    Page<Bills> findByStaffUserIdAndBillDateBetween(@Param("staffId") Integer staffId,
                                                    @Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end,
                                                    Pageable pageable);

    Page<Bills> findAll(Pageable pageable);
}

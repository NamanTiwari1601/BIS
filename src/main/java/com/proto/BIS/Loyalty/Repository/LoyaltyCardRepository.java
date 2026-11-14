package com.proto.BIS.Loyalty.Repository;

import com.proto.BIS.Loyalty.Model.LoyaltyCardModel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LoyaltyCardRepository extends JpaRepository<LoyaltyCardModel, Long> {
    List<LoyaltyCardModel> findAllByOrderByLoyaltyCardIdDesc();

    boolean existsByClient_ClientIdAndService_ServiceId(Integer clientId, Long serviceId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT card FROM LoyaltyCardModel card WHERE card.loyaltyCardId = :id")
    Optional<LoyaltyCardModel> findByIdForUpdate(@Param("id") Long id);
}

package com.proto.BIS.common.Repository;

import com.proto.BIS.common.Model.ClientModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepo extends JpaRepository<ClientModel, Long> {
    Optional<ClientModel> findByClientPhone(String clientPhone);

    List<ClientModel> findByClientEmail(String clientEmail);



    @Query("    SELECT c FROM ClientModel c\n" +
           "    WHERE LOWER(c.clientName) = LOWER(:value)\n" +
           "       OR c.clientPhone = :value\n")
    List<ClientModel> findByNameOrPhone(@Param("value") String value);

    @Query("SELECT c FROM ClientModel c WHERE LOWER(c.clientName) LIKE LOWER(CONCAT('%', :q, '%')) OR c.clientPhone LIKE CONCAT('%', :q, '%')")
    Page<ClientModel> search(@Param("q") String q, Pageable pageable);

    @Query("SELECT b.clientPhNo, COUNT(b), MAX(b.billDate) FROM Bills b WHERE b.clientPhNo IN :phones GROUP BY b.clientPhNo")
    List<Object[]> visitStats(@Param("phones") List<String> phones);

    @Query("SELECT COUNT(c) FROM ClientModel c WHERE (SELECT COUNT(b) FROM Bills b WHERE b.clientPhNo = c.clientPhone) > 1")
    long countReturning();

    long countByCreatedAtGreaterThanEqual(LocalDateTime since);
}
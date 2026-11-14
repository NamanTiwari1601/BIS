package com.proto.BIS.Dealer.Repository;

import com.proto.BIS.Dealer.Model.DealerModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DealerRepository extends JpaRepository<DealerModel, Long> {
    @Query("""
    SELECT d FROM DealerModel d
    WHERE LOWER(d.name) LIKE :pattern OR d.phone LIKE :pattern
    """)
    Page<DealerModel> search(@Param("pattern") String pattern, Pageable pageable);
}

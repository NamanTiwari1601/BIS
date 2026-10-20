package com.proto.BIS.Package.Repository;

import com.proto.BIS.Package.Model.PackageModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PackageRepository extends JpaRepository<PackageModel, Long> {
    @Query(value = """
            SELECT p.*
            FROM s_package p
            WHERE (CAST(:q AS text) IS NULL
                   OR LOWER(p.package_name) LIKE LOWER('%' || CAST(:q AS text) || '%')
                   OR LOWER(COALESCE(p.category, '')) LIKE LOWER('%' || CAST(:q AS text) || '%'))
              AND (CAST(:status AS text) IS NULL OR p.status = CAST(:status AS text))
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM s_package p
            WHERE (CAST(:q AS text) IS NULL
                   OR LOWER(p.package_name) LIKE LOWER('%' || CAST(:q AS text) || '%')
                   OR LOWER(COALESCE(p.category, '')) LIKE LOWER('%' || CAST(:q AS text) || '%'))
              AND (CAST(:status AS text) IS NULL OR p.status = CAST(:status AS text)) 
            """,
            nativeQuery = true)
    Page<PackageModel> search(@Param("q") String q, @Param("status") String status, Pageable pageable);
}
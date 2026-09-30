package com.proto.BIS.common.Repository;

import com.proto.BIS.common.Model.ServiceProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceProductRepository extends JpaRepository<ServiceProduct, Long> {

    List<ServiceProduct> findByService_ServiceId(Long serviceId);
}
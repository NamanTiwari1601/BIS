package com.proto.BIS.common.Repository;

import com.proto.BIS.common.Model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<ProductModel,Integer> {
}

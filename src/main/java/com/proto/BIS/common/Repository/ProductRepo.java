package com.proto.BIS.common.Repository;

import com.Proto.demo.common.Model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepo extends JpaRepository<ProductModel,Integer> {
}

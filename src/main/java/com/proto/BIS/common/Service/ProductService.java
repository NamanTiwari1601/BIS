package com.proto.BIS.common.Service;

import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Repository.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductService {


  public final ProductRepo repo;

    public List<ProductModel> getProducts(){
        log.info("In get products");
        return repo.findAll();
    }

    public Page<ProductModel> getProducts(Pageable pageable){
        log.info("In get paginated products: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repo.findAll(pageable);
    }

    public ProductModel getProductsById(int Prodid){
        log.info("In get product by id: {}", Prodid);
        return repo.findById(Prodid).orElse(new ProductModel());
    }
    public ProductModel addProduct(ProductModel prod){
        log.info("In add product");
        repo.save(prod);
         return prod;
    }
    public void updateProduct(ProductModel prod){
        log.info("In update product");
        repo.save(prod);
    }
    public void deleteProduct(int Prodid){
        log.info("In delete product: {}", Prodid);
        repo.deleteById(Prodid);
    }
}
    
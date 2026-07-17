package com.proto.BIS.common.Service;

import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Repository.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {


  public final ProductRepo repo;

    public List<ProductModel> getProducts(){
        return repo.findAll();
    }

    public ProductModel getProductsById(int Prodid){
        return repo.findById(Prodid).orElse(new ProductModel());
    }
    public ProductModel addProduct(ProductModel prod){
        repo.save(prod);
         return prod;
    }
    public void updateProduct(ProductModel prod){
        repo.save(prod);
    }
    public void deleteProduct(int Prodid){
        repo.deleteById(Prodid);
    }
}

package com.proto.BIS.common.Service;

import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    ProductRepo repo;
//    ArrayList<ProductModel> products= new ArrayList<>(Arrays.asList(
//                new ProductModel(101,"Shampoo",200.00),
//                new ProductModel(102,"Conditioner",400.00),
//                new ProductModel(103,"Facial Set",600.00)));
    public List<ProductModel> getProducts(){
        return repo.findAll();
    }
    public ProductModel getProductsById(int Prodid){
        return repo.findById(Prodid).orElse(new ProductModel());

    }
    public void addProduct(ProductModel prod){
        repo.save(prod);
    }
    public void updateProduct(ProductModel prod){
        repo.save(prod);
    }
    public void deleteProduct(int Prodid){
        repo.deleteById(Prodid);
    }
}

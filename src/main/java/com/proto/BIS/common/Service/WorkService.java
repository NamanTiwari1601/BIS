package com.proto.BIS.common.Service;

import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Repository.WorkRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkService {

    @Autowired
    WorkRepo repo;
//    ArrayList<ServicesModel> products= new ArrayList<>(Arrays.asList(
//                new ServicesModel(101,"Shampoo",200.00),
//                new ServicesModel(102,"Conditioner",400.00),
//                new ServicesModel(103,"Facial Set",600.00)));
    public List<ServicesModel> getProducts(){

        List<ServicesModel> service=repo.findAll();
        return service;
    }
    public ServicesModel getProductsById(int Prodid){
        return repo.findById(Prodid).orElse(new ServicesModel());

    }
    public void addProduct(ServicesModel prod){
        repo.save(prod);
    }
    public void updateProduct(ServicesModel prod){
        repo.save(prod);
    }
    public void deleteProduct(int Prodid){
        repo.deleteById(Prodid);
    }
}

package com.proto.BIS.common.Service;

import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Repository.WorkRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkService {


    public final WorkRepo repo;


    public List<ServicesModel> getProducts(){
        List<ServicesModel> service=repo.findAll();
        return service;
    }
    public ServicesModel getProductsById(int Prodid){
        return repo.findById(Prodid).orElse(new ServicesModel());

    }
    public ServicesModel addProduct(ServicesModel prod){
        return repo.save(prod);
    }
    public ServicesModel updateProduct(ServicesModel prod){
        return repo.save(prod);
    }
    public void deleteProduct(int Prodid){
        repo.deleteById(Prodid);
    }
}

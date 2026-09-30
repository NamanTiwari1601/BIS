package com.proto.BIS.common.Service;

import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Repository.WorkRepo;
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
public class WorkService {


    public final WorkRepo repo;


    public List<ServicesModel> getProducts(){
        log.info("In get services");
        List<ServicesModel> service=repo.findAll();
        return service;
    }

    public Page<ServicesModel> getProducts(Pageable pageable){
        log.info("In get paginated services: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return repo.findAll(pageable);
    }
    public ServicesModel getProductsById(int Prodid){
        log.info("In get service by id: {}", Prodid);
        return repo.findById(Prodid).orElse(new ServicesModel());

    }
    public ServicesModel addProduct(ServicesModel prod){
        log.info("In add service");
        return repo.save(prod);
    }
    public ServicesModel updateProduct(ServicesModel prod){
        log.info("In update service");
        return repo.save(prod);
    }
    public void deleteProduct(int Prodid){
        log.info("In delete service: {}", Prodid);
        repo.deleteById(Prodid);
    }
}

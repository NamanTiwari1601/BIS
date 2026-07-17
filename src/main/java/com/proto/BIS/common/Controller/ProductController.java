package com.proto.BIS.common.Controller;

import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {


public final ProductService service;

@GetMapping("/getProducts")
    public ResponseEntity<List<ProductModel>> ShowProducts(){

    return  ResponseEntity.ok(service.getProducts());
}
@GetMapping("/getProducts/{prodId}")
    public ResponseEntity<ProductModel> getProductbyId(@PathVariable int prodId){
    return ResponseEntity.ok(service.getProductsById(prodId));
    }

    @PostMapping("/addProducts")
    public ResponseEntity<ProductModel> addProduct(@RequestBody ProductModel prod)
    {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addProduct(prod));
    }

    @PutMapping("/updateProduct")
    public void updateProduct(@RequestBody ProductModel prod){
    service.updateProduct(prod);
    }

    @DeleteMapping("/deleteProduct/{prodId}")
    public void  deleteProduct(@PathVariable int prodId){
    service.deleteProduct(prodId);
    }
}


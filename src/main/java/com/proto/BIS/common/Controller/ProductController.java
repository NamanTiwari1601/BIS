package com.proto.BIS.common.Controller;

import com.Proto.demo.common.Model.ProductModel;
import com.Proto.demo.common.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller

public class ProductController {

@Autowired
ProductService service;

@RequestMapping("/Products")
@ResponseBody
    public List<ProductModel> ShowProducts(){

    return service.getProducts();
}
@RequestMapping("/Products/{prodId}")
@ResponseBody
    public ProductModel getProductbyId(@PathVariable int prodId){
    return service.getProductsById(prodId);
    }

    @PostMapping("/addProducts")
    @ResponseBody
    public void addProduct(@RequestBody ProductModel prod){
        service.addProduct(prod);
    }

    @PutMapping("/updateProduct")
    @ResponseBody
    public void updateProduct(@RequestBody ProductModel prod){
    service.updateProduct(prod);
    }

    @DeleteMapping("/deleteProduct/{prodId}")
    public void  deleteProduct(@PathVariable int prodId){
    service.deleteProduct(prodId);
    }
}


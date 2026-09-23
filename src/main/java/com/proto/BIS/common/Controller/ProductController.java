package com.proto.BIS.common.Controller;

import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Product",description = "Manage Products")
public class ProductController {


public final ProductService service;

@GetMapping("/getProducts")
@Operation(summary="show products", description = "returns list of all products")
    public ResponseEntity<List<ProductModel>> ShowProducts(){

    return  ResponseEntity.ok(service.getProducts());
}
    @GetMapping("/getProducts/{prodId}")
    @Operation(summary = "getting product by product id", description = "returns a product model by its product id")
    public ResponseEntity<ProductModel> getProductbyId(@PathVariable int prodId){
    return ResponseEntity.ok(service.getProductsById(prodId));
    }

    @PostMapping("/addProducts")
    @Operation(summary = "Add a product", description = "add an product")
    public ResponseEntity<ProductModel> addProduct(@RequestBody ProductModel prod)
    {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addProduct(prod));
    }

    @PutMapping("/updateProduct")
    @Operation(summary = "update a product's content")
    public void updateProduct(@RequestBody ProductModel prod){
    service.updateProduct(prod);
    }

    @DeleteMapping("/deleteProduct/{prodId}")
    @Operation(summary = "delete a product")
    public void  deleteProduct(@PathVariable int prodId){
    service.deleteProduct(prodId);
    }
}


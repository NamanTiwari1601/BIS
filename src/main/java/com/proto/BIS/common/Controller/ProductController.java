package com.proto.BIS.common.Controller;

import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Tag(name = "Product",description = "Manage Products")
public class ProductController {


public final ProductService service;
private static final Logger log = LoggerFactory.getLogger(ProductController.class);

@GetMapping("/getProducts")
@Operation(summary="show products", description = "returns list of all products")
    public ResponseEntity<List<ProductModel>> ShowProducts(){
    log.info("In show products");

    return  ResponseEntity.ok(service.getProducts());
}

@GetMapping("/page")
@Operation(summary = "get products page", description = "returns paginated products")
public ResponseEntity<Page<ProductModel>> getProductsPage(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    log.info("In get products page: page={}, size={}", page, size);
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(service.getProducts(pageable));
}
    @GetMapping("/getProducts/{prodId}")
    @Operation(summary = "getting product by product id", description = "returns a product model by its product id")
    public ResponseEntity<ProductModel> getProductbyId(@PathVariable int prodId){
    log.info("In get product by id: {}", prodId);
    return ResponseEntity.ok(service.getProductsById(prodId));
    }

    @PostMapping("/addProducts")
    @Operation(summary = "Add a product", description = "add an product")
    public ResponseEntity<ProductModel> addProduct(@RequestBody ProductModel prod)
    {
        log.info("In add product");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addProduct(prod));
    }

    @PutMapping("/updateProduct")
    @Operation(summary = "update a product's content")
    public void updateProduct(@RequestBody ProductModel prod){
    log.info("In update product");
    service.updateProduct(prod);
    }

    @DeleteMapping("/deleteProduct/{prodId}")
    @Operation(summary = "delete a product")
    public void  deleteProduct(@PathVariable int prodId){
    log.info("In delete product: {}", prodId);
    service.deleteProduct(prodId);
    }
}


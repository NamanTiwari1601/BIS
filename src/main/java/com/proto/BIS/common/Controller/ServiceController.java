package com.proto.BIS.common.Controller;

import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Service.WorkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
@CrossOrigin("htttp://localhost:5173")
public class ServiceController {


    public final WorkService service;

    @GetMapping("/services")
    public List<ServicesModel> getServices(){
        return service.getProducts();
    }

    @GetMapping("/service/{serId}")
    public ServicesModel getServiceById(@PathVariable int serId){
        return service.getProductsById(serId);
    }

    @PostMapping("/addService")
    public ResponseEntity<ServicesModel> addService(@Valid @RequestBody ServicesModel model){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addProduct(model));
    }

    @PutMapping("/updateService")
    public ResponseEntity<ServicesModel> updateService(@Valid @RequestBody ServicesModel model){
       return ResponseEntity.ok(service.updateProduct(model));
    }

    @DeleteMapping("/deleteService/{serId}")
    public void deleteProduct(@PathVariable int serId){
        service.deleteProduct(serId);
    }
}

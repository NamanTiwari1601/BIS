package com.proto.BIS.common.Controller;

import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Service.WorkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="Service",description = "Manage Services")
public class ServiceController {


    public final WorkService service;

    @GetMapping("/getServices")
    @Operation(summary = "Get all Service", description = "Returns  a list  of all services")
    public List<ServicesModel> getServices(){
        return service.getProducts();
    }

    @GetMapping("/getService/{serId}")
    @Operation(summary = "Get Service by  id", description = "Returns a service by the service id given to it")
    public ServicesModel getServiceById(@PathVariable int serId){
        return service.getProductsById(serId);
    }

    @PostMapping("/addService")
    @Operation(summary = "Add a new service")
    public ResponseEntity<ServicesModel> addService(@Valid @RequestBody ServicesModel model){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addProduct(model));
    }

    @PutMapping("/updateService")
    @Operation(summary = "Update a service")
    public ResponseEntity<ServicesModel> updateService(@Valid @RequestBody ServicesModel model){
       return ResponseEntity.ok(service.updateProduct(model));
    }
    @DeleteMapping("/deleteService/{serId}")
    @Operation(summary="Delete a service")
    public void deleteProduct(@PathVariable int serId){
        service.deleteProduct(serId);
    }
}

package com.proto.BIS.common.Controller;

import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Service.WorkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/api/service")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
@Tag(name="Service",description = "Manage Services")
public class ServiceController {


    public final WorkService service;
    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);

    @GetMapping("/getServices")
    @Operation(summary = "Get all Service", description = "Returns  a list  of all services")
    public List<ServicesModel> getServices(){
        log.info("In get services");
        return service.getProducts();
    }

    @GetMapping("/page")
    @Operation(summary = "Get paginated services", description = "Returns a page of services")
    public ResponseEntity<Page<ServicesModel>> getServicesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("In get services page: page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.getProducts(pageable));
    }

    @GetMapping("/getService/{serId}")
    @Operation(summary = "Get Service by  id", description = "Returns a service by the service id given to it")
    public ServicesModel getServiceById(@PathVariable int serId){
        log.info("In get service by id: {}", serId);
        return service.getProductsById(serId);
    }

    @PostMapping("/addService")
    @Operation(summary = "Add a new service")
    public ResponseEntity<ServicesModel> addService(@Valid @RequestBody ServicesModel model){
        log.info("In add service");
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addProduct(model));
    }

    @PutMapping("/updateService")
    @Operation(summary = "Update a service")
    public ResponseEntity<ServicesModel> updateService(@Valid @RequestBody ServicesModel model){
       log.info("In update service");
       return ResponseEntity.ok(service.updateProduct(model));
    }
    @DeleteMapping("/deleteService/{serId}")
    @Operation(summary="Delete a service")
    public void deleteProduct(@PathVariable int serId){
        log.info("In delete service: {}", serId);
        service.deleteProduct(serId);
    }
}

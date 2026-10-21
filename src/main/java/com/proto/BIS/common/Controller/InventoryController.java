package com.proto.BIS.common.Controller;

import com.proto.BIS.common.DTO.ServiceProductRequest;
import com.proto.BIS.common.DTO.StockAdjustmentRequest;
import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Model.ServiceProduct;
import com.proto.BIS.common.Service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;

    @PutMapping("/product/{productId}/stock")
    public ResponseEntity<ProductModel> adjustProductStock(
            @PathVariable Integer productId,
            @RequestBody StockAdjustmentRequest request) {
        log.info("Adjusting stock for product {} with delta {}", productId, request.getDelta());
        return ResponseEntity.ok(inventoryService.adjustProductStock(productId, request.getDelta()));
    }

    @GetMapping("/service/{serviceId}/products")
    public ResponseEntity<List<ServiceProduct>> getServiceProducts(
            @PathVariable Integer serviceId) {
        log.info("Getting products linked to service {}", serviceId);
        return ResponseEntity.ok(inventoryService.getServiceProducts(serviceId));
    }

    @PostMapping("/service/{serviceId}/products")
    public ResponseEntity<List<ServiceProduct>> replaceServiceProducts(
            @PathVariable Integer serviceId,
            @RequestBody List<ServiceProductRequest> requests) {
        log.info("Replacing products linked to service {}", serviceId);
        return ResponseEntity.ok(inventoryService.replaceServiceProducts(serviceId, requests));
    }
}
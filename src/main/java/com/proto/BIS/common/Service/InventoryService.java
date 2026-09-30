package com.proto.BIS.common.Service;

import com.proto.BIS.common.DTO.ServiceProductRequest;
import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Model.ServiceProduct;
import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Repository.ProductRepo;
import com.proto.BIS.common.Repository.ServiceProductRepository;
import com.proto.BIS.common.Repository.WorkRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final ProductRepo productRepo;
    private final WorkRepo workRepo;
    private final ServiceProductRepository serviceProductRepository;

    public ProductModel adjustProductStock(Integer productId, Integer delta) {
        if (delta == null) {
            throw new IllegalArgumentException("Stock delta cannot be null");
        }

        ProductModel product = productRepo.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        Long currentQuantity = product.getProductQuantity();
        if (currentQuantity == null) {
            currentQuantity = 0L;
        }

        Long updatedQuantity = currentQuantity + delta.longValue();
        if (updatedQuantity < 0) {
            log.warn("Stock removal rejected for product {}. Current stock: {}, requested delta: {}",
                    productId, currentQuantity, delta);
            throw new IllegalArgumentException("Insufficient stock for product " + productId
                    + ". Available: " + currentQuantity + ", requested delta: " + delta);
        }

        product.setProductQuantity(updatedQuantity);
        ProductModel savedProduct = productRepo.save(product);
        log.info("Stock changed for product {} from {} to {} using delta {}",
                productId, currentQuantity, updatedQuantity, delta);
        return savedProduct;
    }

    @Transactional
    public List<ServiceProduct> getServiceProducts(Integer serviceId) {
        ensureServiceExists(serviceId);
        return serviceProductRepository.findByService_ServiceId(serviceId.longValue());
    }

    @Transactional
    public List<ServiceProduct> replaceServiceProducts(
            Integer serviceId, List<ServiceProductRequest> requests) {
        ServicesModel service = ensureServiceExists(serviceId);

        List<ServiceProduct> existingLinks =
                serviceProductRepository.findByService_ServiceId(serviceId.longValue());
        serviceProductRepository.deleteAll(existingLinks);

        List<ServiceProduct> replacementLinks = requests.stream()
                .map(request -> createServiceProduct(service, request))
                .toList();

        return serviceProductRepository.saveAll(replacementLinks);
    }

    private ServicesModel ensureServiceExists(Integer serviceId) {
        return workRepo.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + serviceId));
    }

    private ServiceProduct createServiceProduct(
            ServicesModel service, ServiceProductRequest request) {
        if (request.getProductId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (request.getRequiredQty() == null || request.getRequiredQty() < 0) {
            throw new IllegalArgumentException("Required quantity must be zero or greater");
        }

        ProductModel product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found: " + request.getProductId()));

        return ServiceProduct.builder()
                .service(service)
                .product(product)
                .requiredQty(request.getRequiredQty())
                .build();
    }
}
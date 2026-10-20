package com.proto.BIS.common.Service;

import com.proto.BIS.common.DTO.ServiceProductRequest;
import com.proto.BIS.common.Model.ProductModel;
import com.proto.BIS.common.Repository.ProductRepo;
import com.proto.BIS.common.Repository.ServiceProductRepository;
import com.proto.BIS.common.Repository.WorkRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    @Mock ProductRepo productRepo;
    @Mock WorkRepo workRepo;
    @Mock ServiceProductRepository links;
    @InjectMocks InventoryService service;

    @Test
    void adjustsStockAndRejectsNegativeResult() {
        ProductModel product = new ProductModel();
        product.setProductQuantity(5L);
        when(productRepo.findById(1)).thenReturn(Optional.of(product));
        when(productRepo.save(product)).thenReturn(product);

        assertSame(product, service.adjustProductStock(1, 3));
        assertEquals(8L, product.getProductQuantity());
        assertThrows(IllegalArgumentException.class, () -> service.adjustProductStock(1, -9));
    }

    @Test
    void rejectsNullDeltaAndMissingProduct() {
        assertThrows(IllegalArgumentException.class, () -> service.adjustProductStock(1, null));
        when(productRepo.findById(1)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.adjustProductStock(1, 1));
    }

    @Test
    void rejectsInvalidServiceProductRequest() {
        assertThrows(IllegalArgumentException.class, () -> service.replaceServiceProducts(1, java.util.List.of(new ServiceProductRequest())));
        verifyNoInteractions(productRepo, links);
    }
}
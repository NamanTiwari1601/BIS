package com.proto.BIS.Package.Service;

import com.proto.BIS.Package.DTO.PackageRequest;
import com.proto.BIS.Package.Model.PackageModel;
import com.proto.BIS.Package.Model.PackageServiceItemModel;
import com.proto.BIS.Package.Repository.PackageRepository;
import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Repository.WorkRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PackageServiceTest {
    @Mock PackageRepository packageRepo;
    @Mock WorkRepo workRepo;
    @InjectMocks PackageService service;

    @Test
    void createsPackageWithServiceItems() {
        PackageRequest request = new PackageRequest();
        request.setPackageName("Combo");
        request.setPackagePrice(50);
        request.setDurationMinutes(60);
        PackageRequest.ServiceItemRequest item = new PackageRequest.ServiceItemRequest();
        item.setServiceId(4L);
        request.setServices(List.of(item));
        ServicesModel serviceModel = ServicesModel.builder().serviceId(4L).serviceName("Facial").servicePrice(50).build();
        when(workRepo.findById(4)).thenReturn(Optional.of(serviceModel));
        when(packageRepo.save(any(PackageModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.create(request);

        assertEquals("Combo", response.getPackageName());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(1, response.getServices().size());
    }

    @Test
    void rejectsEmptyAndDuplicateServices() {
        PackageRequest empty = new PackageRequest();
        assertThrows(IllegalArgumentException.class, () -> service.create(empty));
        PackageRequest duplicate = new PackageRequest();
        PackageRequest.ServiceItemRequest one = new PackageRequest.ServiceItemRequest();
        one.setServiceId(1L);
        PackageRequest.ServiceItemRequest two = new PackageRequest.ServiceItemRequest();
        two.setServiceId(1L);
        duplicate.setServices(List.of(one, two));
        assertThrows(IllegalArgumentException.class, () -> service.create(duplicate));
    }

    @Test
    void rejectsMissingServiceIdOnCreate() {
        PackageRequest request = new PackageRequest();
        PackageRequest.ServiceItemRequest item = new PackageRequest.ServiceItemRequest();
        item.setServiceId(99L);
        request.setServices(List.of(item));
        when(workRepo.findById(99)).thenReturn(Optional.empty());
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> service.create(request));
    }

    @Test
    void updatesExistingServiceItemWithoutCreatingDuplicateLink() {
        ServicesModel serviceModel = ServicesModel.builder().serviceId(2L).serviceName("Facial").servicePrice(50).build();
        PackageServiceItemModel existingItem = PackageServiceItemModel.builder()
                .service(serviceModel).quantity(1).build();
        PackageModel packageModel = PackageModel.builder()
                .packageId(2L).packageName("Combo").services(new java.util.ArrayList<>(List.of(existingItem))).build();
        existingItem.setPackageModel(packageModel);

        PackageRequest request = new PackageRequest();
        request.setPackageName("Updated Combo");
        request.setServices(List.of(serviceItem()));
        when(packageRepo.findById(2L)).thenReturn(Optional.of(packageModel));
        when(workRepo.findById(2)).thenReturn(Optional.of(serviceModel));

        var response = service.update(2L, request);

        assertEquals(1, response.getServices().size());
        assertEquals(3, response.getServices().getFirst().getQuantity());
        verify(packageRepo, never()).save(any(PackageModel.class));
    }

    private PackageRequest.ServiceItemRequest serviceItem() {
        PackageRequest.ServiceItemRequest item = new PackageRequest.ServiceItemRequest();
        item.setServiceId(2L);
        item.setQuantity(3);
        return item;
    }
}
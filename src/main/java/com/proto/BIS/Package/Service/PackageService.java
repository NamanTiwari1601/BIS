package com.proto.BIS.Package.Service;

import com.proto.BIS.Package.DTO.PackageRequest;
import com.proto.BIS.Package.DTO.PackageResponse;
import com.proto.BIS.Package.Model.PackageModel;
import com.proto.BIS.Package.Model.PackageServiceItemModel;
import com.proto.BIS.Package.Repository.PackageRepository;
import com.proto.BIS.common.Model.ServicesModel;
import com.proto.BIS.common.Repository.WorkRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional
public class PackageService {
    private final PackageRepository packageRepository;
    private final WorkRepo serviceRepository;

    @Transactional(readOnly = true)
    public Page<PackageResponse> search(String q, String status, Pageable pageable) {
        return packageRepository.search(blankToNull(q), blankToNull(status), pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PackageResponse get(Long id) {
        return toResponse(find(id));
    }

    public PackageResponse create(PackageRequest request) {
        return save(new PackageModel(), request);
    }

    public PackageResponse update(Long id, PackageRequest request) {
        return save(find(id), request);
    }

    public void delete(Long id) {
        packageRepository.delete(find(id));
    }

    public PackageResponse updateStatus(Long id, String status) {
        if (status == null || status.isBlank()) throw new IllegalArgumentException("Status is required");
        PackageModel model = find(id);
        model.setStatus(status.toUpperCase());
        return toResponse(model);
    }

    private PackageResponse save(PackageModel model, PackageRequest request) {
        if (request.getServices() == null || request.getServices().isEmpty()) {
            throw new IllegalArgumentException("At least one service is required");
        }
        if (new HashSet<>(request.getServices().stream().map(PackageRequest.ServiceItemRequest::getServiceId).toList()).size() != request.getServices().size()) {
            throw new IllegalArgumentException("A service cannot be added more than once to a package");
        }
        model.setPackageName(request.getPackageName());
        model.setDescription(request.getDescription());
        model.setCategory(request.getCategory());
        model.setPackagePrice(request.getPackagePrice());
        model.setDurationMinutes(request.getDurationMinutes());
        model.setStatus(request.getStatus() == null ? "ACTIVE" : request.getStatus().toUpperCase());
        model.getServices().clear();
        for (PackageRequest.ServiceItemRequest item : request.getServices()) {
            ServicesModel service = serviceRepository.findById(item.getServiceId().intValue())
                    .orElseThrow(() -> new EntityNotFoundException("Service not found: " + item.getServiceId()));
            model.getServices().add(PackageServiceItemModel.builder().packageModel(model).service(service).quantity(item.getQuantity()).build());
        }
        return toResponse(packageRepository.save(model));
    }

    private PackageModel find(Long id) {
        return packageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Package not found: " + id));
    }

    private PackageResponse toResponse(PackageModel model) {
        return PackageResponse.builder()
                .packageId(model.getPackageId()).packageName(model.getPackageName()).description(model.getDescription())
                .category(model.getCategory()).packagePrice(model.getPackagePrice()).durationMinutes(model.getDurationMinutes())
                .status(model.getStatus()).createdAt(model.getCreatedAt())
                .services(model.getServices().stream().map(item -> PackageResponse.ServiceResponse.builder()
                        .serviceId(item.getService().getServiceId()).serviceName(item.getService().getServiceName())
                        .quantity(item.getQuantity()).price(item.getService().getServicePrice()).build()).toList())
                .build();
    }

    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value; }
}
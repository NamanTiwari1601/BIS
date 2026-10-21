package com.proto.BIS.Package.Controller;

import com.proto.BIS.Package.DTO.PackageRequest;
import com.proto.BIS.Package.DTO.PackageResponse;
import com.proto.BIS.Package.Service.PackageService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
@Tag(name = "Packages", description = "Create and manage service packages")
public class PackageController {
    private final PackageService packageService;

    @GetMapping
    @Operation(
            summary = "Search packages",
            description = "Returns a paginated list of packages, optionally filtered by name/category text and status.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Packages returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or filter parameters", content = @Content)
    })
    public Page<PackageResponse> getPackages(@RequestParam(required = false) String q,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return packageService.search(q, status, PageRequest.of(page, size, Sort.by("package_name")));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a package", description = "Returns a package and its associated services by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Package found"),
            @ApiResponse(responseCode = "404", description = "Package not found", content = @Content)
    })
    public PackageResponse getPackage(@PathVariable Long id) {
        return packageService.get(id);
    }

    @PostMapping
    @Operation(summary = "Create a package", description = "Creates a package with one or more service associations.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Package created", content = @Content(schema = @Schema(implementation = PackageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid package data, empty services, duplicate services, or unknown service ID", content = @Content)
    })
    public ResponseEntity<PackageResponse> create(@Valid @RequestBody PackageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(packageService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a package", description = "Replaces the package details and service associations for an existing package.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Package updated", content = @Content(schema = @Schema(implementation = PackageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid package data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Package or service not found", content = @Content)
    })
    public PackageResponse update(@PathVariable Long id, @Valid @RequestBody PackageRequest request) {
        return packageService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update package status", description = "Changes the status of an existing package, for example ACTIVE or INACTIVE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Package status updated", content = @Content(schema = @Schema(implementation = PackageResponse.class))),
            @ApiResponse(responseCode = "404", description = "Package not found", content = @Content)
    })
    public PackageResponse updateStatus(@PathVariable Long id, @RequestParam String status) {
        return packageService.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a package", description = "Deletes a package and its package-service associations.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Package deleted"),
            @ApiResponse(responseCode = "404", description = "Package not found", content = @Content)
    })
    public void delete(@PathVariable Long id) {
        packageService.delete(id);
    }
}
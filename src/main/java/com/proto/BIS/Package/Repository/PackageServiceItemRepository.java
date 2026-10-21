package com.proto.BIS.Package.Repository;

import com.proto.BIS.Package.Model.PackageServiceItemModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageServiceItemRepository extends JpaRepository<PackageServiceItemModel, Long> {
}
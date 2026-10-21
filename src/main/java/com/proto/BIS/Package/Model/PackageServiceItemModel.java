package com.proto.BIS.Package.Model;

import com.proto.BIS.common.Model.ServicesModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "s_package_service", uniqueConstraints = @UniqueConstraint(columnNames = {"package_id", "service_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageServiceItemModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageServiceId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private PackageModel packageModel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private ServicesModel service;

    @Column(nullable = false)
    @Builder.Default
    private int quantity = 1;
}
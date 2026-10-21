package com.proto.BIS.Appointment.Model;

import com.proto.BIS.Package.Model.PackageModel;
import com.proto.BIS.common.Model.ServicesModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "s_appointment_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentItemModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appointmentItemId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private AppointmentModel appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private ServicesModel service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private PackageModel packageModel;

    @Column(nullable = false)
    private String itemName;

    private double price;
    private int durationMinutes;
}
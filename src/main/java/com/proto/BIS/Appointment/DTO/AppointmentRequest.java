package com.proto.BIS.Appointment.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class AppointmentRequest {
    @NotNull private Long clientId;
    @NotNull private Integer staffId;
    @NotNull private LocalDate appointmentDate;
    @NotNull private LocalTime startTime;
    @NotNull private LocalTime endTime;
    private String status = "PENDING";
    private String notes;
    @NotEmpty private List<ItemRequest> items;

    @Data
    public static class ItemRequest {
        private Long serviceId;
        private Long packageId;
    }
}
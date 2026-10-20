package com.proto.BIS.Appointment.DTO;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Value
@Builder
public class AppointmentResponse {
    Long appointmentId;
    ClientResponse client;
    StaffResponse staff;
    LocalDate appointmentDate;
    LocalTime startTime;
    LocalTime endTime;
    String status;
    String notes;
    double totalAmount;
    List<ItemResponse> items;

    @Value @Builder
    public static class ClientResponse { Long clientId; String clientName; String clientPhone; }
    @Value @Builder
    public static class StaffResponse { Integer staffId; String staffName; }
    @Value @Builder
    public static class ItemResponse { Long id; String type; String name; double price; int durationMinutes; }
}
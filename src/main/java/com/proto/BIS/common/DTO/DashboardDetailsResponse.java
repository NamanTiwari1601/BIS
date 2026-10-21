package com.proto.BIS.common.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardDetailsResponse {
    private List<AppointmentRow> recentAppointments;
    private List<AppointmentRow> upcomingAppointments;
    private List<TopService> topServices;
    private List<StaffPerformance> staffPerformance;
    private List<InventoryAlert> inventoryAlerts;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AppointmentRow {
        private Long appointmentId;
        private LocalDate date;
        private LocalTime startTime;
        private String clientName;
        private String serviceName;
        private String staffName;
        private String status;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopService {
        private String name;
        private Long bookings;
        private Double sharePct;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StaffPerformance {
        private String staffName;
        private Long appointments;
        private Double revenue;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InventoryAlert {
        private String productName;
        private Long stock;
        private String status;
    }
}
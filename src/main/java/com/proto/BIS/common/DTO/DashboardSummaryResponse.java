package com.proto.BIS.common.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryResponse {
    private Long totalAppointments;
    private Double appointmentsChangePct;
    private Long totalClients;
    private Double clientsChangePct;
    private Double totalRevenue;
    private Double revenueChangePct;
    private Long productsSold;
    private Double productsSoldChangePct;
    private List<DailyCount> appointmentsOverview;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyCount {
        private LocalDate date;
        private Long count;
    }
}
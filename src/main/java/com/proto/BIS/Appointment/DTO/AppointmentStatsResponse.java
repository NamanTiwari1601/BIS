package com.proto.BIS.Appointment.DTO;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AppointmentStatsResponse {
    long total;
    long confirmed;
    long pending;
    long cancelled;
}
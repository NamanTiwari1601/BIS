package com.proto.BIS.common.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientResponse {

    private Integer clientId;
    private String clientName;
    private String clientPhone;
    private String clientEmail;
    private String status;
    private  String notes;
    private LocalDateTime lastVisit;
    private Long totalVisits;
}

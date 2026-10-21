package com.proto.BIS.common.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientStatsResponse {
    private Long totalClients;
    private Long newClients;
    private Long returningClients;
}

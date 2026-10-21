package com.proto.BIS.common.Controller;

import com.proto.BIS.common.DTO.DashboardDetailsResponse;
import com.proto.BIS.common.DTO.DashboardSummaryResponse;
import com.proto.BIS.common.Service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        log.info("In get dashboard summary");
        return ResponseEntity.ok(dashboardService.getSummary());
    }
    @GetMapping("/details")
    public ResponseEntity<DashboardDetailsResponse> getDetails() {
        log.info("In get dashboard details");
        return ResponseEntity.ok(dashboardService.getDetails());
    }
}
package com.karrardelivery.controller;

import com.karrardelivery.constant.ApiUrls;
import com.karrardelivery.dto.AgentTodayDto;
import com.karrardelivery.dto.DashboardMoneyDto;
import com.karrardelivery.dto.DashboardSummaryDto;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.karrardelivery.constant.ApiUrls.*;

@RestController
@RequestMapping(ApiUrls.DASHBOARD)
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /** KPI cards: today vs the same weekday last week, under delivery, delayed */
    @GetMapping(DASHBOARD_SUMMARY)
    public ResponseEntity<GenericResponse<DashboardSummaryDto>> getSummary(
            @RequestParam(value = "days", required = false) Integer days) {
        return ResponseEntity.ok(dashboardService.getSummary(days));
    }

    /** Revenue (delivery fees) and net profit of DELIVERED orders, for a month (yyyy-MM, default this month) and the month before */
    @GetMapping(DASHBOARD_MONEY)
    public ResponseEntity<GenericResponse<DashboardMoneyDto>> getMoney(
            @RequestParam(value = "month", required = false) String month) {
        return ResponseEntity.ok(dashboardService.getMoney(month));
    }

    /** Per agent: assigned / delivered today, under delivery now, cash to collect */
    @GetMapping(DASHBOARD_AGENTS_TODAY)
    public ResponseEntity<GenericResponse<List<AgentTodayDto>>> getAgentsToday() {
        return ResponseEntity.ok(dashboardService.getAgentsToday());
    }
}

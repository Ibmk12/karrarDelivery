package com.karrardelivery.service;

import com.karrardelivery.dto.AgentTodayDto;
import com.karrardelivery.dto.DashboardMoneyDto;
import com.karrardelivery.dto.DashboardSummaryDto;
import com.karrardelivery.dto.GenericResponse;

import java.util.List;

public interface DashboardService {

    GenericResponse<DashboardSummaryDto> getSummary(Integer delayedDays);

    GenericResponse<DashboardMoneyDto> getMoney(String month);

    GenericResponse<List<AgentTodayDto>> getAgentsToday();
}

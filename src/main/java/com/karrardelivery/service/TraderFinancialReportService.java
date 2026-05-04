package com.karrardelivery.service;

import com.karrardelivery.dto.OrderDto;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TraderFinancialReportService {

    byte[] generateTraderFinancialExcelReport(Map<String, List<OrderDto>> grouped, Date reportDate, String[] headerKeys) throws IOException;

}
package com.karrardelivery.service.impl;

import com.karrardelivery.dto.AgentTodayDto;
import com.karrardelivery.dto.DashboardMoneyDto;
import com.karrardelivery.dto.DashboardSummaryDto;
import com.karrardelivery.dto.GenericResponse;
import com.karrardelivery.entity.enums.EDeliveryStatus;
import com.karrardelivery.repository.OrderRepository;
import com.karrardelivery.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

import static com.karrardelivery.constant.Messages.DATA_FETCHED_SUCCESSFULLY;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;

    @Override
    public GenericResponse<DashboardSummaryDto> getSummary(Integer delayedDays) {
        int days = (delayedDays == null || delayedDays <= 0) ? 2 : delayedDays;
        LocalDate today = LocalDate.now();
        LocalDate lastWeek = today.minusWeeks(1);

        DashboardSummaryDto dto = DashboardSummaryDto.builder()
                .createdToday(orderRepository.countByOrderDateGreaterThanEqualAndOrderDateLessThan(
                        startOf(today), startOf(today.plusDays(1))))
                .createdSameDayLastWeek(orderRepository.countByOrderDateGreaterThanEqualAndOrderDateLessThan(
                        startOf(lastWeek), startOf(lastWeek.plusDays(1))))
                .deliveredToday(orderRepository.countByDeliveryStatusAndDeliveryDateGreaterThanEqualAndDeliveryDateLessThan(
                        EDeliveryStatus.DELIVERED, startOf(today), startOf(today.plusDays(1))))
                .deliveredSameDayLastWeek(orderRepository.countByDeliveryStatusAndDeliveryDateGreaterThanEqualAndDeliveryDateLessThan(
                        EDeliveryStatus.DELIVERED, startOf(lastWeek), startOf(lastWeek.plusDays(1))))
                .underDelivery(orderRepository.countByDeliveryStatus(EDeliveryStatus.UNDER_DELIVERY))
                // same rule as GET /orders/under-delivery/longer-than
                .delayed(orderRepository.countByDeliveryStatusAndOrderDateBefore(
                        EDeliveryStatus.UNDER_DELIVERY, startOf(today.minusDays(days))))
                .delayedDays(days)
                .build();

        return GenericResponse.successResponse(dto, DATA_FETCHED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<DashboardMoneyDto> getMoney(String month) {
        YearMonth ym = YearMonth.now();
        if (month != null && !month.isBlank()) {
            try {
                ym = YearMonth.parse(month.trim());
            } catch (DateTimeParseException e) {
                log.warn("Invalid month '{}', using the current month", month);
            }
        }
        DashboardMoneyDto current = moneyFor(ym);
        current.setPrevious(moneyFor(ym.minusMonths(1)));
        return GenericResponse.successResponse(current, DATA_FETCHED_SUCCESSFULLY);
    }

    @Override
    public GenericResponse<List<AgentTodayDto>> getAgentsToday() {
        LocalDate today = LocalDate.now();
        List<AgentTodayDto> result = orderRepository.findAgentsActivity(
                        startOf(today), startOf(today.plusDays(1)),
                        EDeliveryStatus.DELIVERED, EDeliveryStatus.UNDER_DELIVERY)
                .stream()
                .map(row -> AgentTodayDto.builder()
                        .agentId(((Number) row[0]).longValue())
                        .agentName((String) row[1])
                        .phoneNumber((String) row[2])
                        .assignedToday(toLong(row[3]))
                        .deliveredToday(toLong(row[4]))
                        .underDelivery(toLong(row[5]))
                        .cashToCollect(toDouble(row[6]))
                        .build())
                .toList();
        return GenericResponse.successResponse(result, DATA_FETCHED_SUCCESSFULLY);
    }

    private DashboardMoneyDto moneyFor(YearMonth ym) {
        List<Object[]> rows = orderRepository.sumMoneyBetween(
                EDeliveryStatus.DELIVERED, startOf(ym.atDay(1)), startOf(ym.plusMonths(1).atDay(1)));
        Object[] row = rows.isEmpty() ? new Object[]{0L, 0d, 0d} : rows.get(0);
        return DashboardMoneyDto.builder()
                .month(ym.toString())
                .deliveredCount(toLong(row[0]))
                .revenue(toDouble(row[1]))
                .netProfit(toDouble(row[2]))
                .build();
    }

    /** Start of the day in the server's time zone (same as the other order queries). */
    private static Date startOf(LocalDate day) {
        return Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static long toLong(Object value) {
        return value == null ? 0L : ((Number) value).longValue();
    }

    private static double toDouble(Object value) {
        return value == null ? 0d : ((Number) value).doubleValue();
    }
}

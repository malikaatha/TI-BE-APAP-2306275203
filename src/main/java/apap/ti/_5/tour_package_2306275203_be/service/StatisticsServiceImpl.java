package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.response.RevenueStatisticsResponseDTO;
import apap.ti._5.tour_package_2306275203_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306275203_be.model.Plan;
import apap.ti._5.tour_package_2306275203_be.repository.OrderedQuantityDb;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final OrderedQuantityDb orderedQuantityDb;

    public StatisticsServiceImpl(OrderedQuantityDb orderedQuantityDb) {
        this.orderedQuantityDb = orderedQuantityDb;
    }

    @Override
    public RevenueStatisticsResponseDTO getRevenueStatistics(Integer year, Integer month) {
        if (year == null) {
            throw new IllegalArgumentException("Parameter 'year' is required.");
        }

        if (month != null) {
            return getMonthlyRevenue(year, month);
        } else {
            return getYearlyRevenue(year);
        }
    }

    private RevenueStatisticsResponseDTO getMonthlyRevenue(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.withDayOfMonth(start.toLocalDate().lengthOfMonth()).with(LocalTime.MAX);

        List<OrderedQuantity> orders = orderedQuantityDb.findByStartDateBetween(start, end).stream()
                .filter(oq -> {
                    Plan plan = oq.getPlan();
                    return plan != null && "Fulfilled".equalsIgnoreCase(plan.getStatus());
                })
                .collect(Collectors.toList());

        Map<String, Long> breakdown = orders.stream()
                .collect(Collectors.groupingBy(
                        oq -> oq.getActivity().getActivityType(),
                        Collectors.summingLong(oq -> oq.getPrice() * oq.getOrderedQuota())
                ));

        Long totalRevenue = breakdown.values().stream().mapToLong(Long::longValue).sum();

        return new RevenueStatisticsResponseDTO(
                year + "-" + month,
                totalRevenue,
                breakdown
        );
    }

    private RevenueStatisticsResponseDTO getYearlyRevenue(int year) {
        LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        List<OrderedQuantity> orders = orderedQuantityDb.findByStartDateBetween(start, end).stream()
                .filter(oq -> {
                    Plan plan = oq.getPlan();
                    return plan != null && "Fulfilled".equalsIgnoreCase(plan.getStatus());
                })
                .collect(Collectors.toList());

        Map<String, Long> breakdown = new LinkedHashMap<>();
        for (Month m : Month.values()) {
            breakdown.put(m.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), 0L);
        }

        for (OrderedQuantity oq : orders) {
            String monthName = oq.getStartDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            long revenue = oq.getPrice() * oq.getOrderedQuota();
            breakdown.put(monthName, breakdown.get(monthName) + revenue);
        }

        Long totalRevenue = breakdown.values().stream().mapToLong(Long::longValue).sum();

        return new RevenueStatisticsResponseDTO(
                String.valueOf(year),
                totalRevenue,
                breakdown
        );
    }
}
package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.response.RevenueStatisticsResponseDTO;
import java.util.List;

public interface StatisticsService {
    public RevenueStatisticsResponseDTO getRevenueStatistics(Integer year, Integer month);
}
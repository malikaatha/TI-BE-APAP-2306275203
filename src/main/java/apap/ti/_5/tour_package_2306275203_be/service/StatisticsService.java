package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.response.RevenueByActivityTypeDTO;
import java.util.List;

public interface StatisticsService {
    List<RevenueByActivityTypeDTO> getPotentialRevenue(Integer year, Integer month);
}
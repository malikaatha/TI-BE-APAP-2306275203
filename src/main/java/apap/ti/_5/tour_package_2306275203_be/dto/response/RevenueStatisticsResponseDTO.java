package apap.ti._5.tour_package_2306275203_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RevenueStatisticsResponseDTO {
    private String period;
    private Long totalRevenue;
    private Object breakdown;
}
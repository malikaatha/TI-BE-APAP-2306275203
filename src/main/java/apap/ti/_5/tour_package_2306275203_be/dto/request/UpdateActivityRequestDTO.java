package apap.ti._5.tour_package_2306275203_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateActivityRequestDTO {
    private String activityName;
    private String activityItem;
    private Integer capacity;
    private Long price;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String startLocation;
    private String endLocation;
}
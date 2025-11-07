package apap.ti._5.tour_package_2306275203_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityResponseDTO {
    private String id;
    private String activityName;
    private String activityItem;
    private int capacity;
    private Long price;
    private String activityType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String startLocation;
    private String endLocation;
}
package apap.ti._5.tour_package_2306275203_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderedQuantityResponseDTO {
    private UUID id;
    private int orderedQuota;
    private Long price;
    private String activityName;
    private String activityId;

    private int capacity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
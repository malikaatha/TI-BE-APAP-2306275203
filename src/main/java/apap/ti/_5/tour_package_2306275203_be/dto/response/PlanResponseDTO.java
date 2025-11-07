package apap.ti._5.tour_package_2306275203_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlanResponseDTO {
    private UUID id;
    private String planName;
    private Long price;
    private String activityType;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String startLocation;
    private String endLocation;
    private String packageId;
    private List<OrderedQuantityResponseDTO> listOrderedQuantity; 

}
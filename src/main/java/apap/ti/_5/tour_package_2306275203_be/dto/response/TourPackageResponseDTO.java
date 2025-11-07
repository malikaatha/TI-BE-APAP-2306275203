package apap.ti._5.tour_package_2306275203_be.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TourPackageResponseDTO {
    private String id;
    private String userId;
    private String packageName;
    private int quota;
    private Long price;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<PlanResponseDTO> listPlan; 

}
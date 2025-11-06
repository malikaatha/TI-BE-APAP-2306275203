package apap.ti._5.tour_package_2306275203_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderedQuantityRequestDTO {
    private UUID planId;
    private String activityId;
    private int orderedQuota;
}
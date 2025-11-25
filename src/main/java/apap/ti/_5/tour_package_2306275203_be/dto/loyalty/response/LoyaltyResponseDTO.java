package apap.ti._5.tour_package_2306275203_be.dto.loyalty.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class LoyaltyResponseDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponResponseDTO {
        private UUID id;
        private String name;
        private String description;
        private int points;
        private int percentOff;
        private LocalDateTime createdDate;
        private LocalDateTime updatedDate;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoyaltyPointsResponseDTO {
        private UUID customerId;
        private int points;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchasedCouponResponseDTO {
        private UUID id;
        private String code;
        private UUID customerId;
        private CouponResponseDTO coupon;
        private LocalDateTime purchasedDate;
        private LocalDateTime usedDate;
    }
}
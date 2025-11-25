package apap.ti._5.tour_package_2306275203_be.dto.loyalty.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

public class LoyaltyRequestDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCouponRequestDTO {
        private String name;
        private String description;
        private int points;
        private int percentOff;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCouponRequestDTO {
        private String name;
        private String description;
        private int points;
        private int percentOff;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseCouponRequestDTO {
        private UUID customerId;
        private UUID couponId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddPointsRequestDTO {
        private UUID customerId;
        private int addLoyaltyPoints;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UseCouponRequestDTO {
        private String purchasedCouponCode;
        private UUID customerId;
    }
}
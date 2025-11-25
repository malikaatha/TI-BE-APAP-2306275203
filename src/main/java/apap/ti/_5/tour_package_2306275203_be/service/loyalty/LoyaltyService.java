package apap.ti._5.tour_package_2306275203_be.service.loyalty;

import apap.ti._5.tour_package_2306275203_be.dto.loyalty.request.LoyaltyRequestDTO.*;
import apap.ti._5.tour_package_2306275203_be.dto.loyalty.response.LoyaltyResponseDTO.*;

import java.util.List;
import java.util.UUID;

public interface LoyaltyService {
    List<CouponResponseDTO> getAllAvailableCoupons();
    
    List<PurchasedCouponResponseDTO> getAllPurchasedCoupons(UUID customerId);
    
    LoyaltyPointsResponseDTO getCustomerLoyaltyPoints(UUID customerId);
    
    CouponResponseDTO createCoupon(CreateCouponRequestDTO dto);
    
    CouponResponseDTO updateCoupon(UUID couponId, UpdateCouponRequestDTO dto);
    
    PurchasedCouponResponseDTO purchaseCoupon(PurchaseCouponRequestDTO dto);
    
    LoyaltyPointsResponseDTO addLoyaltyPoints(AddPointsRequestDTO dto);
    
    int useCoupon(UseCouponRequestDTO dto);
}
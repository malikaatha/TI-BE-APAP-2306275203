package apap.ti._5.tour_package_2306275203_be.controller;

import apap.ti._5.tour_package_2306275203_be.dto.loyalty.request.LoyaltyRequestDTO.*;
import apap.ti._5.tour_package_2306275203_be.dto.loyalty.response.LoyaltyResponseDTO.*;
import apap.ti._5.tour_package_2306275203_be.service.loyalty.LoyaltyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/loyalty")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    public LoyaltyController(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    // PBI-BE-L1 
    @GetMapping("/coupons")
    public ResponseEntity<List<CouponResponseDTO>> getAllAvailableCoupons() {
        return ResponseEntity.ok(loyaltyService.getAllAvailableCoupons());
    }

    // PBI-BE-L2
    @GetMapping("/purchased-coupons/{customerId}")
    public ResponseEntity<List<PurchasedCouponResponseDTO>> getAllPurchasedCoupons(@PathVariable UUID customerId) {
        return ResponseEntity.ok(loyaltyService.getAllPurchasedCoupons(customerId));
    }

    // PBI-BE-L3
    @GetMapping("/points/{customerId}")
    public ResponseEntity<LoyaltyPointsResponseDTO> getCustomerLoyaltyPoints(@PathVariable UUID customerId) {
        return ResponseEntity.ok(loyaltyService.getCustomerLoyaltyPoints(customerId));
    }

    // PBI-BE-L4
    @PostMapping("/coupon")
    public ResponseEntity<CouponResponseDTO> createCoupon(@RequestBody CreateCouponRequestDTO dto) {
        return ResponseEntity.ok(loyaltyService.createCoupon(dto));
    }

    // PBI-BE-L5
    @PutMapping("/coupon/{couponId}")
    public ResponseEntity<CouponResponseDTO> updateCoupon(
            @PathVariable UUID couponId,
            @RequestBody UpdateCouponRequestDTO dto) {
        return ResponseEntity.ok(loyaltyService.updateCoupon(couponId, dto));
    }

    // PBI-BE-L6
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseCoupon(@RequestBody PurchaseCouponRequestDTO dto) {
        try {
            return ResponseEntity.ok(loyaltyService.purchaseCoupon(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PBI-BE-L7 (Dipanggil oleh Bill Service)
    @PostMapping("/points/add")
    public ResponseEntity<LoyaltyPointsResponseDTO> addLoyaltyPoints(
            @RequestBody AddPointsRequestDTO dto) {
        return ResponseEntity.ok(loyaltyService.addLoyaltyPoints(dto));
    }

    // PBI-BE-L8
    @PostMapping("/use-coupon")
    public ResponseEntity<Integer> useCoupon(@RequestBody UseCouponRequestDTO dto) {
        return ResponseEntity.ok(loyaltyService.useCoupon(dto));
    }
}
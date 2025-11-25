package apap.ti._5.tour_package_2306275203_be.service.loyalty;

import apap.ti._5.tour_package_2306275203_be.dto.loyalty.request.LoyaltyRequestDTO.*;
import apap.ti._5.tour_package_2306275203_be.dto.loyalty.response.LoyaltyResponseDTO.*;
import apap.ti._5.tour_package_2306275203_be.model.loyalty.Coupon;
import apap.ti._5.tour_package_2306275203_be.model.loyalty.LoyaltyPoints;
import apap.ti._5.tour_package_2306275203_be.model.loyalty.PurchasedCoupon;
import apap.ti._5.tour_package_2306275203_be.repository.loyalty.CouponDb;
import apap.ti._5.tour_package_2306275203_be.repository.loyalty.LoyaltyPointsDb;
import apap.ti._5.tour_package_2306275203_be.repository.loyalty.PurchasedCouponDb;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoyaltyServiceImpl implements LoyaltyService {

    private final CouponDb couponDb;
    private final LoyaltyPointsDb loyaltyPointsDb;
    private final PurchasedCouponDb purchasedCouponDb;

    public LoyaltyServiceImpl(CouponDb couponDb, LoyaltyPointsDb loyaltyPointsDb, PurchasedCouponDb purchasedCouponDb) {
        this.couponDb = couponDb;
        this.loyaltyPointsDb = loyaltyPointsDb;
        this.purchasedCouponDb = purchasedCouponDb;
    }

    @Override
    public List<CouponResponseDTO> getAllAvailableCoupons() {
        return couponDb.findByIsDeletedFalse().stream()
                .map(this::convertCouponToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchasedCouponResponseDTO> getAllPurchasedCoupons(UUID customerId) {
        return purchasedCouponDb.findByCustomerId(customerId).stream()
                .map(this::convertPurchasedCouponToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LoyaltyPointsResponseDTO getCustomerLoyaltyPoints(UUID customerId) {
        LoyaltyPoints lp = loyaltyPointsDb.findByCustomerId(customerId)
                .orElse(LoyaltyPoints.builder().customerId(customerId).points(0).build());
        
        return LoyaltyPointsResponseDTO.builder()
                .customerId(lp.getCustomerId())
                .points(lp.getPoints())
                .build();
    }

    @Override
    public CouponResponseDTO createCoupon(CreateCouponRequestDTO dto) {
        Coupon coupon = Coupon.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .points(dto.getPoints())
                .percentOff(dto.getPercentOff())
                .isDeleted(false)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .build();
        return convertCouponToDTO(couponDb.save(coupon));
    }

    @Override
    public CouponResponseDTO updateCoupon(UUID couponId, UpdateCouponRequestDTO dto) {
        Coupon coupon = couponDb.findById(couponId)
                .orElseThrow(() -> new NoSuchElementException("Coupon not found"));

        coupon.setName(dto.getName());
        coupon.setDescription(dto.getDescription());
        coupon.setPoints(dto.getPoints());
        coupon.setPercentOff(dto.getPercentOff());

        return convertCouponToDTO(couponDb.save(coupon));
    }

    @Override
    public PurchasedCouponResponseDTO purchaseCoupon(PurchaseCouponRequestDTO dto) {
        // 1. Get Customer Points
        LoyaltyPoints customerPoints = loyaltyPointsDb.findByCustomerId(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer does not have loyalty points record yet."));

        // 2. Get Coupon
        Coupon coupon = couponDb.findById(dto.getCouponId())
                .orElseThrow(() -> new NoSuchElementException("Coupon not found"));

        // 3. Validation: Points sufficiency
        if (customerPoints.getPoints() < coupon.getPoints()) {
            throw new IllegalStateException("Insufficient loyalty points.");
        }

        // 4. Deduct Points
        customerPoints.setPoints(customerPoints.getPoints() - coupon.getPoints());
        loyaltyPointsDb.save(customerPoints);

        // 5. Generate Unique Code
        // Format: [5 char coupon name] - [5 char customer name] - [total purchased]
        // Note: Karena tidak ada akses ke DB Profile untuk nama customer, kita gunakan 5 char pertama UUID customer sebagai fallback
        String couponPart = getFirstNCharacters(coupon.getName(), 5);
        String customerPart = getFirstNCharacters(dto.getCustomerId().toString(), 5); 
        long count = purchasedCouponDb.count() + 1;
        
        String uniqueCode = String.format("%s-%s-%d", couponPart, customerPart, count).toUpperCase();

        // 6. Create Purchased Coupon
        PurchasedCoupon purchasedCoupon = PurchasedCoupon.builder()
                .coupon(coupon)
                .customerId(dto.getCustomerId())
                .code(uniqueCode)
                .build();

        return convertPurchasedCouponToDTO(purchasedCouponDb.save(purchasedCoupon));
    }

    @Override
    public LoyaltyPointsResponseDTO addLoyaltyPoints(AddPointsRequestDTO dto) {
        LoyaltyPoints lp = loyaltyPointsDb.findByCustomerId(dto.getCustomerId())
                .orElseGet(() -> LoyaltyPoints.builder()
                        .customerId(dto.getCustomerId())
                        .points(0)
                        .build());

        lp.setPoints(lp.getPoints() + dto.getAddLoyaltyPoints());
        LoyaltyPoints saved = loyaltyPointsDb.save(lp);

        return LoyaltyPointsResponseDTO.builder()
                .customerId(saved.getCustomerId())
                .points(saved.getPoints())
                .build();
    }

    @Override
    public int useCoupon(UseCouponRequestDTO dto) {
        try {
            PurchasedCoupon pc = purchasedCouponDb.findByCode(dto.getPurchasedCouponCode())
                    .orElseThrow(() -> new NoSuchElementException("Coupon code not found"));

            // Validasi kepemilikan
            if (!pc.getCustomerId().equals(dto.getCustomerId())) {
                return 0; // Tidak valid
            }

            // Validasi sudah dipakai atau belum
            if (pc.getUsedDate() != null) {
                return 0; // Sudah dipakai
            }

            // Update usedDate
            pc.setUsedDate(LocalDateTime.now());
            purchasedCouponDb.save(pc);

            return pc.getCoupon().getPercentOff();

        } catch (Exception e) {
            return 0;
        }
    }

    // Helper Methods
    private CouponResponseDTO convertCouponToDTO(Coupon coupon) {
        return CouponResponseDTO.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .description(coupon.getDescription())
                .points(coupon.getPoints())
                .percentOff(coupon.getPercentOff())
                .createdDate(coupon.getCreatedDate())
                .updatedDate(coupon.getUpdatedDate())
                .build();
    }

    private PurchasedCouponResponseDTO convertPurchasedCouponToDTO(PurchasedCoupon pc) {
        return PurchasedCouponResponseDTO.builder()
                .id(pc.getId())
                .code(pc.getCode())
                .customerId(pc.getCustomerId())
                .coupon(convertCouponToDTO(pc.getCoupon()))
                .purchasedDate(pc.getPurchasedDate())
                .usedDate(pc.getUsedDate())
                .build();
    }

    private String getFirstNCharacters(String str, int n) {
        if (str == null) return "XXXXX";
        String cleanStr = str.replaceAll("[^a-zA-Z0-9]", ""); // remove symbols for code clarity
        if (cleanStr.length() < n) return cleanStr;
        return cleanStr.substring(0, n);
    }
}
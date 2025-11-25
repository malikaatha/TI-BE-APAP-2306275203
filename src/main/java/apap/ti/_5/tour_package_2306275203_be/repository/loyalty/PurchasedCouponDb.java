package apap.ti._5.tour_package_2306275203_be.repository.loyalty;

import apap.ti._5.tour_package_2306275203_be.model.loyalty.PurchasedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchasedCouponDb extends JpaRepository<PurchasedCoupon, UUID> {
    List<PurchasedCoupon> findByCustomerId(UUID customerId);
    Optional<PurchasedCoupon> findByCode(String code);
    long count();
}
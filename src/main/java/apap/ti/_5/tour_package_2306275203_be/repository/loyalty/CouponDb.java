package apap.ti._5.tour_package_2306275203_be.repository.loyalty;

import apap.ti._5.tour_package_2306275203_be.model.loyalty.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CouponDb extends JpaRepository<Coupon, UUID> {
    List<Coupon> findByIsDeletedFalse();
}
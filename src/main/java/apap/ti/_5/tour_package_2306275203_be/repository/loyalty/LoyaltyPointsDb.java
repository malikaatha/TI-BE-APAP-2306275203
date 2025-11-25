package apap.ti._5.tour_package_2306275203_be.repository.loyalty;

import apap.ti._5.tour_package_2306275203_be.model.loyalty.LoyaltyPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoyaltyPointsDb extends JpaRepository<LoyaltyPoints, UUID> {
    Optional<LoyaltyPoints> findByCustomerId(UUID customerId);
}
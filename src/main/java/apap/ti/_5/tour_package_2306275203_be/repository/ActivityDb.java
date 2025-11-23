package apap.ti._5.tour_package_2306275203_be.repository;

import apap.ti._5.tour_package_2306275203_be.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityDb extends JpaRepository<Activity, String> {
        List<Activity> findByActivityTypeAndStartLocationAndEndLocationAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
        String activityType, String startLocation, String endLocation, java.time.LocalDateTime planStartDate, java.time.LocalDateTime planEndDate);

    Optional<Activity> findTopByIdStartingWithOrderByIdDesc(String prefix);
}
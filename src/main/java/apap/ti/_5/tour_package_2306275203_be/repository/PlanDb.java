package apap.ti._5.tour_package_2306275203_be.repository;

import apap.ti._5.tour_package_2306275203_be.model.Activity;
import apap.ti._5.tour_package_2306275203_be.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PlanDb extends JpaRepository<Plan, UUID> {
        List<Activity> findByActivityTypeAndStartLocationAndEndLocationAndStartDateAfterAndEndDateBefore(
        String activityType, String startLocation, String endLocation, LocalDateTime planStartDate, LocalDateTime planEndDate);
}
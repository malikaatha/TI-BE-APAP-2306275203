package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.response.ActivityResponseDTO;
import apap.ti._5.tour_package_2306275203_be.model.Activity;
import apap.ti._5.tour_package_2306275203_be.model.Plan;
import apap.ti._5.tour_package_2306275203_be.repository.ActivityDb;
import apap.ti._5.tour_package_2306275203_be.repository.PlanDb;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ActivityServiceImpl implements ActivityService {
        private final ActivityDb activityDb;

        private final PlanDb planDb;

        public ActivityServiceImpl(ActivityDb activityDb, PlanDb planDb) {
                this.activityDb = activityDb;
                this.planDb = planDb;
        }

    @Override
    public List<ActivityResponseDTO> getAllActivities() {
        return activityDb.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ActivityResponseDTO> getFilteredActivitiesForPlan(UUID planId) {
        Plan plan = planDb.findById(planId)
                .orElseThrow(() -> new NoSuchElementException("Plan not found"));
        

        List<Activity> filteredActivities = activityDb.findByActivityTypeAndStartLocationAndEndLocationAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
                plan.getActivityType(),
                plan.getStartLocation(),
                plan.getEndLocation(),
                plan.getStartDate(),
                plan.getEndDate()
        );

        return filteredActivities.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    private ActivityResponseDTO convertToResponseDTO(Activity activity) {
        return new ActivityResponseDTO(
                activity.getId(),
                activity.getActivityName(),
                activity.getActivityItem(),
                activity.getCapacity(),
                activity.getPrice(),
                activity.getActivityType(),
                activity.getStartDate(),
                activity.getEndDate(),
                activity.getStartLocation(),
                activity.getEndLocation()
        );
    }
}
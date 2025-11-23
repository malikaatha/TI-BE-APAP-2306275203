package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreateActivityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdateActivityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.ActivityResponseDTO;
import apap.ti._5.tour_package_2306275203_be.model.Activity;
import apap.ti._5.tour_package_2306275203_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306275203_be.model.Plan;
import apap.ti._5.tour_package_2306275203_be.repository.ActivityDb;
import apap.ti._5.tour_package_2306275203_be.repository.PlanDb;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Override
    public ActivityResponseDTO createActivity(CreateActivityRequestDTO dto) {
        if (dto.getActivityName() == null || dto.getActivityName().isEmpty()) throw new IllegalArgumentException("Activity Name is required");
        if (dto.getActivityType() == null || dto.getActivityType().isEmpty()) throw new IllegalArgumentException("Activity Type is required");

        if (dto.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0.");
        }
        if (dto.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }

        if (dto.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past.");
        }


        if (!dto.getEndDate().isAfter(dto.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date.");
        }

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String prefix = "ACT-" + dateStr + "-";

        var latestActivity = activityDb.findLatestIdIncludingDeleted(prefix);

        int nextSequence = 1;
        if (latestActivity.isPresent()) {
            String lastId = latestActivity.get().getId();
            try {
                String lastSequenceStr = lastId.substring(lastId.length() - 3);
                nextSequence = Integer.parseInt(lastSequenceStr) + 1;
            } catch (Exception e) {
                nextSequence = 1;
            }
        }

        String newId = String.format("%s%03d", prefix, nextSequence);

        Activity activity = new Activity();
        activity.setId(newId);
        activity.setActivityName(dto.getActivityName());
        activity.setActivityItem(dto.getActivityItem());
        activity.setCapacity(dto.getCapacity());
        activity.setPrice(dto.getPrice());
        activity.setActivityType(dto.getActivityType());
        activity.setStartDate(dto.getStartDate());
        activity.setEndDate(dto.getEndDate());
        activity.setStartLocation(dto.getStartLocation());
        activity.setEndLocation(dto.getEndLocation());

        Activity savedActivity = activityDb.save(activity);

        return convertToResponseDTO(savedActivity);
    }

        @Override
        public ActivityResponseDTO updateActivity(String id, UpdateActivityRequestDTO dto) {
                Activity activity = activityDb.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("Activity not found"));

                if (activity.getListOrderedQuantity() != null) {
                for (OrderedQuantity oq : activity.getListOrderedQuantity()) {
                        Plan plan = oq.getPlan();
                        if (plan != null && "Fulfilled".equalsIgnoreCase(plan.getStatus())) {
                        throw new IllegalStateException("Cannot update activity that belongs to a fulfilled plan.");
                        }
                }
                }

                if (dto.getPrice() <= 0) {
                throw new IllegalArgumentException("Price must be greater than 0.");
                }
                if (dto.getCapacity() <= 0) {
                throw new IllegalArgumentException("Capacity must be greater than 0.");
                }
                
                // StartDate >= Now
                if (dto.getStartDate().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Start date cannot be in the past.");
                }

                // StartDate < EndDate
                if (!dto.getEndDate().isAfter(dto.getStartDate())) {
                throw new IllegalArgumentException("End date must be after start date.");
                }

                // Update Field (Backlog 2 & 3)
                activity.setActivityName(dto.getActivityName());
                activity.setActivityItem(dto.getActivityItem());
                activity.setPrice(dto.getPrice());
                activity.setCapacity(dto.getCapacity());
                activity.setStartDate(dto.getStartDate());
                activity.setEndDate(dto.getEndDate());
                activity.setStartLocation(dto.getStartLocation());
                activity.setEndLocation(dto.getEndLocation());

                Activity savedActivity = activityDb.save(activity);
                
                return convertToResponseDTO(savedActivity);
        }

@Override
    public void deleteActivity(String id) {
        Activity activity = activityDb.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Activity not found"));

        if (activity.getListOrderedQuantity() != null && !activity.getListOrderedQuantity().isEmpty()) {
            for (OrderedQuantity oq : activity.getListOrderedQuantity()) {
                Plan plan = oq.getPlan();
                
                if (plan != null && !"Fulfilled".equalsIgnoreCase(plan.getStatus())) {
                    throw new IllegalStateException("Cannot delete activity that is linked to Unfulfilled plans.");
                }
            }
        }

        activityDb.delete(activity);
    }
}
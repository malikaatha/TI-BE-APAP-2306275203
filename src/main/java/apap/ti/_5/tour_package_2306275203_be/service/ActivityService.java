package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreateActivityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdateActivityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.ActivityResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ActivityService {
    List<ActivityResponseDTO> getAllActivities(
        boolean includeDeleted,
        String activityType,
        String startLocation,
        String endLocation,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String search
    );
    List<ActivityResponseDTO> getFilteredActivitiesForPlan(UUID planId);
    ActivityResponseDTO createActivity(CreateActivityRequestDTO dto);
    ActivityResponseDTO updateActivity(String id, UpdateActivityRequestDTO dto);
    void deleteActivity(String id);
    public ActivityResponseDTO getActivityById(String id);
}
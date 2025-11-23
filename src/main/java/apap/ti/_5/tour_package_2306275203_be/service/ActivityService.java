package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreateActivityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdateActivityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.ActivityResponseDTO;
import java.util.List;
import java.util.UUID;

public interface ActivityService {
    List<ActivityResponseDTO> getAllActivities();
    List<ActivityResponseDTO> getFilteredActivitiesForPlan(UUID planId);
    ActivityResponseDTO createActivity(CreateActivityRequestDTO dto);
    ActivityResponseDTO updateActivity(String id, UpdateActivityRequestDTO dto);
}
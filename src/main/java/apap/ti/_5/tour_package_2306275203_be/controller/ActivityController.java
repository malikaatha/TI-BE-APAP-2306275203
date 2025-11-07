package apap.ti._5.tour_package_2306275203_be.controller;

import apap.ti._5.tour_package_2306275203_be.dto.response.ActivityResponseDTO;
import apap.ti._5.tour_package_2306275203_be.service.ActivityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/activities")
@CrossOrigin(origins = "http://localhost:5173")
public class ActivityController {
    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("")
    public ResponseEntity<?> getActivities(@RequestParam(value = "planId", required = false) UUID planId) {
        try {
            List<ActivityResponseDTO> activities;
            if (planId != null) {
                // Jika ada planId, panggil fungsi filter
                activities = activityService.getFilteredActivitiesForPlan(planId);
            } else {
                // Jika tidak, tampilkan semua (untuk debugging/admin)
                activities = activityService.getAllActivities();
            }
            return ResponseEntity.ok(activities);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
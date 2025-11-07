package apap.ti._5.tour_package_2306275203_be;

import apap.ti._5.tour_package_2306275203_be.dto.response.ActivityResponseDTO;
import apap.ti._5.tour_package_2306275203_be.model.Activity;
import apap.ti._5.tour_package_2306275203_be.model.Plan;
import apap.ti._5.tour_package_2306275203_be.repository.ActivityDb;
import apap.ti._5.tour_package_2306275203_be.repository.PlanDb;
import apap.ti._5.tour_package_2306275203_be.service.ActivityServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceImplTest {

    @InjectMocks
    private ActivityServiceImpl activityService;

    @Mock
    private ActivityDb activityDb;

    @Mock
    private PlanDb planDb;

    private Activity activity1;
    private Plan plan;
    private UUID planId;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        planId = UUID.randomUUID();

        activity1 = new Activity();
        activity1.setId("ACT-001");
        activity1.setActivityName("Snorkeling at Gili Trawangan");
        activity1.setActivityItem("Snorkeling Gear");
        activity1.setCapacity(15);
        activity1.setPrice(250000L);
        activity1.setActivityType("Water Sport");
        activity1.setStartDate(now.plusDays(2));
        activity1.setEndDate(now.plusDays(2));
        activity1.setStartLocation("Lombok");
        activity1.setEndLocation("Gili Trawangan");

        plan = new Plan();
        plan.setId(planId);
        plan.setActivityType("Water Sport");
        plan.setStartLocation("Lombok");
        plan.setEndLocation("Gili Trawangan");
        plan.setStartDate(now.plusDays(1));
        plan.setEndDate(now.plusDays(5));
    }


    @Test
    void testGetAllActivities_Success() {
        when(activityDb.findAll()).thenReturn(List.of(activity1));

        List<ActivityResponseDTO> result = activityService.getAllActivities();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(activity1.getActivityName(), result.get(0).getActivityName());
        assertEquals(activity1.getId(), result.get(0).getId());
        verify(activityDb, times(1)).findAll();
    }

    @Test
    void testGetAllActivities_EmptyList() {
        when(activityDb.findAll()).thenReturn(Collections.emptyList());

        List<ActivityResponseDTO> result = activityService.getAllActivities();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(activityDb, times(1)).findAll();
    }


    @Test
    void testGetFilteredActivitiesForPlan_Success() {
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));
        when(activityDb.findByActivityTypeAndStartLocationAndEndLocationAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
                plan.getActivityType(),
                plan.getStartLocation(),
                plan.getEndLocation(),
                plan.getStartDate(),
                plan.getEndDate()
        )).thenReturn(List.of(activity1));

        List<ActivityResponseDTO> result = activityService.getFilteredActivitiesForPlan(planId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(activity1.getId(), result.get(0).getId());
        verify(planDb, times(1)).findById(planId);
        verify(activityDb, times(1)).findByActivityTypeAndStartLocationAndEndLocationAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
            anyString(), anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)
        );
    }
    
    @Test
    void testGetFilteredActivitiesForPlan_PlanNotFound() {
        when(planDb.findById(planId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> activityService.getFilteredActivitiesForPlan(planId));
        verify(planDb, times(1)).findById(planId);
        verify(activityDb, never()).findByActivityTypeAndStartLocationAndEndLocationAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
            anyString(), anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)
        );
    }

    @Test
    void testGetFilteredActivitiesForPlan_NoMatchingActivities() {
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));
        when(activityDb.findByActivityTypeAndStartLocationAndEndLocationAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
                anyString(), anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(Collections.emptyList());

        List<ActivityResponseDTO> result = activityService.getFilteredActivitiesForPlan(planId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(planDb, times(1)).findById(planId);
    }
}
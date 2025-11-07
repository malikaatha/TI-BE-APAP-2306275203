package apap.ti._5.tour_package_2306275203_be;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreatePlanRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdatePlanRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.PlanResponseDTO;
import apap.ti._5.tour_package_2306275203_be.model.*;
import apap.ti._5.tour_package_2306275203_be.repository.PlanDb;
import apap.ti._5.tour_package_2306275203_be.repository.TourPackageDb;
import apap.ti._5.tour_package_2306275203_be.service.PlanServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanServiceImplTest {

    @InjectMocks
    private PlanServiceImpl planService;

    @Mock
    private PlanDb planDb;

    @Mock
    private TourPackageDb tourPackageDb;

    private TourPackage tourPackage;
    private Plan plan;
    private CreatePlanRequestDTO createPlanRequestDTO;
    private UpdatePlanRequestDTO updatePlanRequestDTO;
    private UUID planId;
    private String packageId;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        packageId = "PACK-123-001";
        planId = UUID.randomUUID();

        tourPackage = new TourPackage();
        tourPackage.setId(packageId);
        tourPackage.setStatus("Pending");
        tourPackage.setStartDate(now.plusDays(1));
        tourPackage.setEndDate(now.plusDays(10));
        tourPackage.setListPlan(new ArrayList<>());

        plan = new Plan();
        plan.setId(planId);
        plan.setPlanName("City Tour");
        plan.setTourPackage(tourPackage);
        plan.setListOrderedQuantity(new ArrayList<>());

        createPlanRequestDTO = new CreatePlanRequestDTO();
        createPlanRequestDTO.setPlanName("Mountain Hike");
        createPlanRequestDTO.setActivityType("Adventure");
        createPlanRequestDTO.setStartDate(now.plusDays(2));
        createPlanRequestDTO.setEndDate(now.plusDays(3));
        createPlanRequestDTO.setStartLocation("Base Camp");
        createPlanRequestDTO.setEndLocation("Summit");

        updatePlanRequestDTO = new UpdatePlanRequestDTO();
        updatePlanRequestDTO.setPlanName("Updated City Tour");
        updatePlanRequestDTO.setStartDate(now.plusDays(4));
        updatePlanRequestDTO.setEndDate(now.plusDays(5));
        updatePlanRequestDTO.setStartLocation("Museum");
        updatePlanRequestDTO.setEndLocation("Park");
    }

    @Test
    void testCreatePlan_Success() {
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        when(planDb.save(any(Plan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlanResponseDTO response = planService.createPlan(packageId, createPlanRequestDTO);

        assertNotNull(response);
        assertEquals(createPlanRequestDTO.getPlanName(), response.getPlanName());
        assertEquals(0L, response.getPrice());
        assertEquals("Unfulfilled", response.getStatus());
        verify(tourPackageDb, times(1)).findById(packageId);
        verify(planDb, times(1)).save(any(Plan.class));
    }

    @Test
    void testCreatePlan_PackageNotFound() {
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> planService.createPlan(packageId, createPlanRequestDTO));
        verify(tourPackageDb, times(1)).findById(packageId);
        verify(planDb, never()).save(any(Plan.class));
    }

    @Test
    void testCreatePlan_PackageNotPending() {
        tourPackage.setStatus("Processed");
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));

        assertThrows(IllegalStateException.class, () -> planService.createPlan(packageId, createPlanRequestDTO));
    }

    @Test
    void testCreatePlan_InvalidStartDate_BeforePackage() {
        createPlanRequestDTO.setStartDate(now); 
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));

        assertThrows(IllegalArgumentException.class, () -> planService.createPlan(packageId, createPlanRequestDTO));
    }

    @Test
    void testCreatePlan_InvalidEndDate_AfterPackage() {
        createPlanRequestDTO.setEndDate(now.plusDays(11)); 
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));

        assertThrows(IllegalArgumentException.class, () -> planService.createPlan(packageId, createPlanRequestDTO));
    }
    
    @Test
    void testCreatePlan_InvalidEndDate_BeforeStartDate() {
        createPlanRequestDTO.setEndDate(now.plusDays(1));
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));

        assertThrows(IllegalArgumentException.class, () -> planService.createPlan(packageId, createPlanRequestDTO));
    }

    @Test
    void testCreatePlan_AccommodationLocationMismatch() {
        createPlanRequestDTO.setActivityType("Accommodation");
        createPlanRequestDTO.setStartLocation("Hotel A");
        createPlanRequestDTO.setEndLocation("Hotel B"); 
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));

        assertThrows(IllegalArgumentException.class, () -> planService.createPlan(packageId, createPlanRequestDTO));
    }

    @Test
    void testGetPlanById_Success() {
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));

        PlanResponseDTO response = planService.getPlanById(planId);

        assertNotNull(response);
        assertEquals(plan.getId(), response.getId());
        assertEquals(plan.getPlanName(), response.getPlanName());
        assertTrue(response.getListOrderedQuantity().isEmpty());
    }
    
    @Test
    void testGetPlanById_Success_WithOrderedQuantities() {
        Activity activity = new Activity();
        activity.setId("ACT-001");
        activity.setActivityName("Snorkeling");
        activity.setCapacity(10);

        OrderedQuantity oq = new OrderedQuantity();
        oq.setId(UUID.randomUUID());
        oq.setOrderedQuota(2);
        oq.setPrice(100L);
        oq.setActivity(activity);
        oq.setStartDate(now.plusDays(2));
        oq.setEndDate(now.plusDays(2));
        plan.setListOrderedQuantity(List.of(oq));

        when(planDb.findById(planId)).thenReturn(Optional.of(plan));

        PlanResponseDTO response = planService.getPlanById(planId);

        assertNotNull(response);
        // REVISI: Menggunakan getListOrderedQuantity() sesuai DTO
        assertEquals(1, response.getListOrderedQuantity().size());
        assertEquals("Snorkeling", response.getListOrderedQuantity().get(0).getActivityName());
    }

    @Test
    void testGetPlanById_Success_WithNullOrderedQuantities() {
        plan.setListOrderedQuantity(null);
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));

        PlanResponseDTO response = planService.getPlanById(planId);

        assertNotNull(response);
        assertTrue(response.getListOrderedQuantity().isEmpty());
    }

    @Test
    void testGetPlanById_NotFound() {
        when(planDb.findById(planId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> planService.getPlanById(planId));
    }

    // --- Tests for updatePlan ---
    @Test
    void testUpdatePlan_Success() {
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));
        when(planDb.save(any(Plan.class))).thenReturn(plan);

        PlanResponseDTO response = planService.updatePlan(planId, updatePlanRequestDTO);

        assertNotNull(response);
        assertEquals(updatePlanRequestDTO.getPlanName(), response.getPlanName());
        assertEquals(updatePlanRequestDTO.getStartDate(), response.getStartDate());
        verify(planDb, times(1)).save(plan);
    }

    @Test
    void testUpdatePlan_NotFound() {
        when(planDb.findById(planId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> planService.updatePlan(planId, updatePlanRequestDTO));
        verify(planDb, never()).save(any(Plan.class));
    }

    @Test
    void testUpdatePlan_PackageNotPending() {
        tourPackage.setStatus("Processed");
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));

        assertThrows(IllegalStateException.class, () -> planService.updatePlan(planId, updatePlanRequestDTO));
        verify(planDb, never()).save(any(Plan.class));
    }

    @Test
    void testUpdatePlan_HasOrderedActivities() {
        plan.setListOrderedQuantity(List.of(new OrderedQuantity()));
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));

        assertThrows(IllegalStateException.class, () -> planService.updatePlan(planId, updatePlanRequestDTO));
        verify(planDb, never()).save(any(Plan.class));
    }

 
    @Test
    void testDeletePlan_Success() {
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));
        doNothing().when(planDb).delete(plan);

        planService.deletePlan(planId);

        verify(planDb, times(1)).findById(planId);
        verify(planDb, times(1)).delete(plan);
    }

    @Test
    void testDeletePlan_NotFound() {
        when(planDb.findById(planId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> planService.deletePlan(planId));
        verify(planDb, never()).delete(any(Plan.class));
    }

    @Test
    void testDeletePlan_PackageNotPending() {
        tourPackage.setStatus("Processed");
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));

        assertThrows(IllegalStateException.class, () -> planService.deletePlan(planId));
        verify(planDb, never()).delete(any(Plan.class));
    }
}
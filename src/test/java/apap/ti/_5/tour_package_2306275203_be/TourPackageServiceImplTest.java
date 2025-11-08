package apap.ti._5.tour_package_2306275203_be;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreateTourPackageRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdateTourPackageRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.TourPackageResponseDTO;
import apap.ti._5.tour_package_2306275203_be.model.*;
import apap.ti._5.tour_package_2306275203_be.repository.ActivityDb;
import apap.ti._5.tour_package_2306275203_be.repository.TourPackageDb;
import apap.ti._5.tour_package_2306275203_be.service.TourPackageServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TourPackageServiceImplTest {

    @InjectMocks
    private TourPackageServiceImpl tourPackageService;

    @Mock
    private TourPackageDb tourPackageDb;

    @Mock
    private ActivityDb activityDb;

    private TourPackage tourPackage;
    private CreateTourPackageRequestDTO createDto;
    private UpdateTourPackageRequestDTO updateDto;
    private LocalDateTime now;
    private String packageId;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        packageId = "PACK-USER1-001";

        createDto = new CreateTourPackageRequestDTO();
        createDto.setUserId("USER1");
        createDto.setPackageName("Bali Adventure");
        createDto.setQuota(10);
        createDto.setStartDate(now.plusDays(1));
        createDto.setEndDate(now.plusDays(5));

        updateDto = new UpdateTourPackageRequestDTO();
        updateDto.setPackageName("Updated Bali Adventure");
        updateDto.setQuota(15);
        updateDto.setStartDate(now.plusDays(2));
        updateDto.setEndDate(now.plusDays(6));

        tourPackage = TourPackage.builder()
                .id(packageId)
                .userId("USER1")
                .packageName("Bali Adventure")
                .quota(10)
                .price(0L)
                .status("Pending")
                .startDate(now.plusDays(1))
                .endDate(now.plusDays(5))
                .listPlan(new ArrayList<>())
                .build();
    }

@Test
void testCreateTourPackage_Success() {
    when(tourPackageDb.findLatestIdByUserId(anyString())).thenReturn(Collections.emptyList());
    
    when(tourPackageDb.save(any(TourPackage.class))).thenAnswer(invocation -> invocation.getArgument(0));

    TourPackageResponseDTO response = tourPackageService.createTourPackage(createDto);

    assertNotNull(response);
    assertEquals("PACK-USER1-001", response.getId());
    assertEquals("Pending", response.getStatus());
    
    verify(tourPackageDb, times(1)).findLatestIdByUserId("USER1");
    verify(tourPackageDb, times(1)).save(any(TourPackage.class));
}

    @Test
    void testCreateTourPackage_InvalidDates() {
        createDto.setEndDate(now);
        assertThrows(IllegalArgumentException.class, () -> tourPackageService.createTourPackage(createDto));
        verify(tourPackageDb, never()).save(any(TourPackage.class));
    }

    @Test
    void testGetAllTourPackage_Success() {
        when(tourPackageDb.findAll()).thenReturn(List.of(tourPackage));
        List<TourPackageResponseDTO> responses = tourPackageService.getAllTourPackage();
        assertEquals(1, responses.size());
        assertEquals(packageId, responses.get(0).getId());
    }

    @Test
    void testGetAllTourPackage_Empty() {
        when(tourPackageDb.findAll()).thenReturn(Collections.emptyList());
        List<TourPackageResponseDTO> responses = tourPackageService.getAllTourPackage();
        assertTrue(responses.isEmpty());
    }

    @Test
    void testGetTourPackageById_Success() {
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        TourPackageResponseDTO response = tourPackageService.getTourPackageById(packageId);
        assertNotNull(response);
        assertEquals(packageId, response.getId());
    }
    
    @Test
    void testGetTourPackageById_Success_WithNullPlanList() {
        tourPackage.setListPlan(null); // To cover the null check in convertToResponseDTO
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        TourPackageResponseDTO response = tourPackageService.getTourPackageById(packageId);
        assertNotNull(response);
        assertTrue(response.getListPlan().isEmpty());
    }
    
    @Test
    void testGetTourPackageById_Success_WithPopulatedPlanList() {
        Plan plan = new Plan();
        plan.setId(UUID.randomUUID());
        plan.setTourPackage(tourPackage);
        plan.setListOrderedQuantity(new ArrayList<>());
        tourPackage.setListPlan(List.of(plan));

        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        TourPackageResponseDTO response = tourPackageService.getTourPackageById(packageId);
        assertNotNull(response);
        assertEquals(1, response.getListPlan().size());
    }

    @Test
    void testGetTourPackageById_NotFound() {
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> tourPackageService.getTourPackageById(packageId));
    }

    @Test
    void testUpdateTourPackage_Success() {
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        when(tourPackageDb.save(any(TourPackage.class))).thenReturn(tourPackage);

        TourPackageResponseDTO response = tourPackageService.updateTourPackage(packageId, updateDto);
        assertEquals(updateDto.getPackageName(), response.getPackageName());
        assertEquals(updateDto.getQuota(), response.getQuota());
        verify(tourPackageDb, times(1)).save(any(TourPackage.class));
    }

    @Test
    void testUpdateTourPackage_NotFound() {
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> tourPackageService.updateTourPackage(packageId, updateDto));
    }

    @Test
    void testUpdateTourPackage_NotPending() {
        tourPackage.setStatus("Processed");
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        assertThrows(IllegalStateException.class, () -> tourPackageService.updateTourPackage(packageId, updateDto));
    }

    @Test
    void testUpdateTourPackage_HasPlans() {
        tourPackage.setListPlan(List.of(new Plan()));
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        assertThrows(IllegalStateException.class, () -> tourPackageService.updateTourPackage(packageId, updateDto));
    }

    @Test
    void testDeleteTourPackage_Success() {
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        doNothing().when(tourPackageDb).delete(tourPackage);
        tourPackageService.deleteTourPackage(packageId);
        verify(tourPackageDb, times(1)).delete(tourPackage);
    }

    @Test
    void testDeleteTourPackage_NotFound() {
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> tourPackageService.deleteTourPackage(packageId));
    }

    @Test
    void testDeleteTourPackage_NotPending() {
        tourPackage.setStatus("Processed");
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        assertThrows(IllegalStateException.class, () -> tourPackageService.deleteTourPackage(packageId));
    }

    @Test
    void testProcessPackage_Success() {
        Activity activity = new Activity();
        activity.setId("ACT-1");
        activity.setActivityName("Hiking");
        activity.setCapacity(10);
        
        OrderedQuantity oq = new OrderedQuantity();
        oq.setActivity(activity);
        oq.setOrderedQuota(5);
        
        Plan plan = new Plan();
        plan.setStatus("Fulfilled");
        plan.setListOrderedQuantity(List.of(oq));
        plan.setTourPackage(tourPackage); 

        tourPackage.setListPlan(List.of(plan));

        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        when(activityDb.save(any(Activity.class))).thenReturn(activity);
        when(tourPackageDb.save(any(TourPackage.class))).thenReturn(tourPackage);

        TourPackageResponseDTO response = tourPackageService.processPackage(packageId);

        assertEquals("Processed", response.getStatus());
        assertEquals(5, activity.getCapacity()); // 10 - 5
        verify(activityDb, times(1)).save(activity);
        verify(tourPackageDb, times(1)).save(tourPackage);
    }

    @Test
    void testProcessPackage_NotFound() {
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> tourPackageService.processPackage(packageId));
    }

    @Test
    void testProcessPackage_NotPending() {
        tourPackage.setStatus("Cancelled");
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        assertThrows(IllegalStateException.class, () -> tourPackageService.processPackage(packageId));
    }
    
    @Test
    void testProcessPackage_NoPlans_NullList() {
        tourPackage.setListPlan(null); 
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        assertThrows(IllegalStateException.class, () -> tourPackageService.processPackage(packageId));
    }
    
    @Test
    void testProcessPackage_NoPlans_EmptyList() {
        tourPackage.setListPlan(Collections.emptyList()); 
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        assertThrows(IllegalStateException.class, () -> tourPackageService.processPackage(packageId));
    }

    @Test
    void testProcessPackage_PlanNotFulfilled() {
        Plan plan = new Plan();
        plan.setStatus("Unfulfilled");
        tourPackage.setListPlan(List.of(plan));
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        assertThrows(IllegalStateException.class, () -> tourPackageService.processPackage(packageId));
    }

    @Test
    void testProcessPackage_NotEnoughCapacity() {
        Activity activity = new Activity();
        activity.setActivityName("Diving");
        activity.setCapacity(4);

        OrderedQuantity oq = new OrderedQuantity();
        oq.setActivity(activity);
        oq.setOrderedQuota(5);

        Plan plan = new Plan();
        plan.setStatus("Fulfilled");
        plan.setListOrderedQuantity(List.of(oq));

        tourPackage.setListPlan(List.of(plan));
        when(tourPackageDb.findById(packageId)).thenReturn(Optional.of(tourPackage));
        assertThrows(IllegalStateException.class, () -> tourPackageService.processPackage(packageId));
    }
}
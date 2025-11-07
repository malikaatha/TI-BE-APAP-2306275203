// Direvisi sesuai permintaan: package name
package apap.ti._5.tour_package_2306275203_be;

// Direvisi sesuai permintaan: import paths
import apap.ti._5.tour_package_2306275203_be.dto.request.CreateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.model.*;
import apap.ti._5.tour_package_2306275203_be.repository.ActivityDb;
import apap.ti._5.tour_package_2306275203_be.repository.OrderedQuantityDb;
import apap.ti._5.tour_package_2306275203_be.repository.PlanDb;
import apap.ti._5.tour_package_2306275203_be.repository.TourPackageDb;
import apap.ti._5.tour_package_2306275203_be.service.OrderedQuantityServiceImpl;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderedQuantityServiceImplTest {

    @InjectMocks
    private OrderedQuantityServiceImpl orderedQuantityService;

    @Mock
    private OrderedQuantityDb orderedQuantityDb;
    @Mock
    private PlanDb planDb;
    @Mock
    private ActivityDb activityDb;
    @Mock
    private TourPackageDb tourPackageDb;

    private TourPackage tourPackage;
    private Plan plan;
    private Activity activity;
    private OrderedQuantity orderedQuantity;
    private UUID planId, orderedQuantityId;
    private String activityId;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        planId = UUID.randomUUID();
        activityId = "ACT-" + UUID.randomUUID().toString();
        orderedQuantityId = UUID.randomUUID();

        tourPackage = new TourPackage();
        tourPackage.setId("PACK-1");
        tourPackage.setQuota(10);
        tourPackage.setStatus("Pending");

        plan = new Plan();
        plan.setId(planId);
        plan.setActivityType("Adventure");
        plan.setStartDate(now.plusDays(1));
        plan.setEndDate(now.plusDays(5));
        plan.setTourPackage(tourPackage);
        plan.setListOrderedQuantity(new ArrayList<>());

        // FIX 2: Set relasi dua arah. Beri tahu package bahwa ia memiliki plan ini.
        tourPackage.setListPlan(List.of(plan));

        activity = new Activity();
        activity.setId(activityId);
        activity.setActivityType("Adventure");
        activity.setCapacity(20);
        activity.setPrice(100L); // Harga aktivitas
        activity.setStartDate(now.plusDays(2));
        activity.setEndDate(now.plusDays(3));

        orderedQuantity = new OrderedQuantity();
        orderedQuantity.setId(orderedQuantityId);
        orderedQuantity.setPlan(plan);
        orderedQuantity.setActivity(activity);
        orderedQuantity.setOrderedQuota(5);
        // FIX 1: Set harga pada orderedQuantity, meniru logika service
        orderedQuantity.setPrice(activity.getPrice());

        plan.getListOrderedQuantity().add(orderedQuantity);
    }

    // --- Sisa kode test tetap sama ---
    // ... (kode test dari jawaban sebelumnya tidak perlu diubah) ...
    
    @Test
    void testAddActivityToPlan_Success_Fulfilled() {
        CreateOrderedQuantityRequestDTO dto = new CreateOrderedQuantityRequestDTO(activityId, 5);
        
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));
        when(activityDb.findById(activityId)).thenReturn(Optional.of(activity));
        
        when(planDb.findById(plan.getId())).thenReturn(Optional.of(plan));
        List<OrderedQuantity> updatedOQs = new ArrayList<>(plan.getListOrderedQuantity());
        
        OrderedQuantity newOq = new OrderedQuantity();
        newOq.setPrice(activity.getPrice()); // Pastikan OQ baru juga punya harga
        newOq.setOrderedQuota(dto.getOrderedQuota());
        updatedOQs.add(newOq);
        
        when(orderedQuantityDb.findByPlan(plan)).thenReturn(updatedOQs);

        orderedQuantityService.addActivityToPlan(planId, dto);

        verify(orderedQuantityDb, times(1)).save(any(OrderedQuantity.class));
        verify(planDb, times(1)).save(any(Plan.class));
        verify(tourPackageDb, times(1)).save(any(TourPackage.class));
        assertEquals("Fulfilled", plan.getStatus());
    }

    @Test
    void testAddActivityToPlan_Success_Unfulfilled() {
        CreateOrderedQuantityRequestDTO dto = new CreateOrderedQuantityRequestDTO(activityId, 2);
        
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));
        when(activityDb.findById(activityId)).thenReturn(Optional.of(activity));
        when(planDb.findById(plan.getId())).thenReturn(Optional.of(plan));
        when(orderedQuantityDb.findByPlan(plan)).thenReturn(plan.getListOrderedQuantity());

        orderedQuantityService.addActivityToPlan(planId, dto);

        verify(orderedQuantityDb, times(1)).save(any(OrderedQuantity.class));
        assertEquals("Unfulfilled", plan.getStatus());
    }

    @Test
    void testAddActivityToPlan_PlanNotFound() {
        when(planDb.findById(planId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> orderedQuantityService.addActivityToPlan(planId, new CreateOrderedQuantityRequestDTO()));
    }

    @Test
    void testAddActivityToPlan_ActivityNotFound() {
        CreateOrderedQuantityRequestDTO dto = new CreateOrderedQuantityRequestDTO(activityId, 1);
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));
        when(activityDb.findById(activityId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> orderedQuantityService.addActivityToPlan(planId, dto));
    }

    @Test
    void testAddActivityToPlan_PackageNotPending() {
        CreateOrderedQuantityRequestDTO dto = new CreateOrderedQuantityRequestDTO(activityId, 1);
        tourPackage.setStatus("Processed");
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));
        when(activityDb.findById(activityId)).thenReturn(Optional.of(activity));
        assertThrows(IllegalStateException.class, () -> orderedQuantityService.addActivityToPlan(planId, dto));
    }
    
    @Test
    void testAddActivityToPlan_ActivityTypeMismatch() {
        CreateOrderedQuantityRequestDTO dto = new CreateOrderedQuantityRequestDTO(activityId, 1);
        activity.setActivityType("Leisure");
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));
        when(activityDb.findById(activityId)).thenReturn(Optional.of(activity));
        assertThrows(IllegalArgumentException.class, () -> orderedQuantityService.addActivityToPlan(planId, dto));
    }

    @Test
    void testAddActivityToPlan_StartDateInvalid() {
        CreateOrderedQuantityRequestDTO dto = new CreateOrderedQuantityRequestDTO(activityId, 1);
        activity.setStartDate(now); 
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));
        when(activityDb.findById(activityId)).thenReturn(Optional.of(activity));
        assertThrows(IllegalArgumentException.class, () -> orderedQuantityService.addActivityToPlan(planId, dto));
    }

    @Test
    void testAddActivityToPlan_EndDateInvalid() {
        CreateOrderedQuantityRequestDTO dto = new CreateOrderedQuantityRequestDTO(activityId, 1);
        activity.setEndDate(now.plusDays(6));
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));
        when(activityDb.findById(activityId)).thenReturn(Optional.of(activity));
        assertThrows(IllegalArgumentException.class, () -> orderedQuantityService.addActivityToPlan(planId, dto));
    }

    @Test
    void testAddActivityToPlan_CapacityExceeded() {
        CreateOrderedQuantityRequestDTO dto = new CreateOrderedQuantityRequestDTO(activityId, 21);
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));
        when(activityDb.findById(activityId)).thenReturn(Optional.of(activity));
        assertThrows(IllegalArgumentException.class, () -> orderedQuantityService.addActivityToPlan(planId, dto));
    }

    @Test
    void testAddActivityToPlan_PackageQuotaExceeded() {
        CreateOrderedQuantityRequestDTO dto = new CreateOrderedQuantityRequestDTO(activityId, 6);
        when(planDb.findById(planId)).thenReturn(Optional.of(plan));
        when(activityDb.findById(activityId)).thenReturn(Optional.of(activity));
        assertThrows(IllegalArgumentException.class, () -> orderedQuantityService.addActivityToPlan(planId, dto));
    }

    @Test
    void testUpdateOrderedQuantity_Success() {
        UpdateOrderedQuantityRequestDTO dto = new UpdateOrderedQuantityRequestDTO(2);
        
        when(orderedQuantityDb.findById(orderedQuantityId)).thenReturn(Optional.of(orderedQuantity));
        when(planDb.findById(plan.getId())).thenReturn(Optional.of(plan));
        when(orderedQuantityDb.findByPlan(plan)).thenReturn(plan.getListOrderedQuantity());

        orderedQuantityService.updateOrderedQuantity(orderedQuantityId, dto);

        assertEquals(2, orderedQuantity.getOrderedQuota());
        verify(orderedQuantityDb, times(1)).save(orderedQuantity);
    }

    @Test
    void testUpdateOrderedQuantity_NotFound() {
        when(orderedQuantityDb.findById(orderedQuantityId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> orderedQuantityService.updateOrderedQuantity(orderedQuantityId, new UpdateOrderedQuantityRequestDTO()));
    }

    @Test
    void testRemoveActivityFromPlan_Success() {
        when(orderedQuantityDb.findById(orderedQuantityId)).thenReturn(Optional.of(orderedQuantity));
        when(planDb.findById(plan.getId())).thenReturn(Optional.of(plan));
        when(orderedQuantityDb.findByPlan(plan)).thenReturn(Collections.emptyList());

        orderedQuantityService.removeActivityFromPlan(orderedQuantityId);

        assertTrue(plan.getListOrderedQuantity().isEmpty());
        verify(orderedQuantityDb, times(1)).delete(orderedQuantity);
        verify(planDb, times(1)).save(plan);
    }
    
    @Test
    void testRemoveActivityFromPlan_NotFound() {
        when(orderedQuantityDb.findById(orderedQuantityId)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> orderedQuantityService.removeActivityFromPlan(orderedQuantityId));
    }

    @Test
    void testRemoveActivityFromPlan_PackageNotPending() {
        tourPackage.setStatus("Processed");
        when(orderedQuantityDb.findById(orderedQuantityId)).thenReturn(Optional.of(orderedQuantity));
        assertThrows(IllegalStateException.class, () -> orderedQuantityService.removeActivityFromPlan(orderedQuantityId));
    }
}
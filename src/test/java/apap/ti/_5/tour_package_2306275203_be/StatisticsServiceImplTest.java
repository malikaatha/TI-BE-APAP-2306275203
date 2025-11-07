package apap.ti._5.tour_package_2306275203_be;

import apap.ti._5.tour_package_2306275203_be.dto.response.RevenueByActivityTypeDTO;
import apap.ti._5.tour_package_2306275203_be.model.Activity;
import apap.ti._5.tour_package_2306275203_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306275203_be.repository.OrderedQuantityDb;
import apap.ti._5.tour_package_2306275203_be.service.StatisticsServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Mock
    private OrderedQuantityDb orderedQuantityDb;

    private OrderedQuantity oqAdventure1, oqAdventure2, oqLeisure1;

    @BeforeEach
    void setUp() {
        Activity activityAdventure = new Activity();
        activityAdventure.setActivityType("Adventure");

        Activity activityLeisure = new Activity();
        activityLeisure.setActivityType("Leisure");

        oqAdventure1 = new OrderedQuantity();
        oqAdventure1.setActivity(activityAdventure);
        oqAdventure1.setPrice(100L);
        oqAdventure1.setOrderedQuota(2);

        oqAdventure2 = new OrderedQuantity();
        oqAdventure2.setActivity(activityAdventure);
        oqAdventure2.setPrice(50L);
        oqAdventure2.setOrderedQuota(3);

        oqLeisure1 = new OrderedQuantity();
        oqLeisure1.setActivity(activityLeisure);
        oqLeisure1.setPrice(200L);
        oqLeisure1.setOrderedQuota(4);
    }

    @Test
    void testGetPotentialRevenue_withYearAndMonth() {
        Integer year = 2023;
        Integer month = 10;
        List<OrderedQuantity> mockOrders = List.of(oqAdventure1, oqLeisure1);
        when(orderedQuantityDb.findByYearAndMonth(year, month)).thenReturn(mockOrders);
        
        List<RevenueByActivityTypeDTO> result = statisticsService.getPotentialRevenue(year, month);

        
        assertNotNull(result);
        assertEquals(2, result.size());
        
        Optional<RevenueByActivityTypeDTO> adventureRevenue = result.stream()
            .filter(r -> "Adventure".equals(r.getActivityType())).findFirst();
        assertTrue(adventureRevenue.isPresent());

        assertEquals(200L, adventureRevenue.get().getTotalRevenue());

        Optional<RevenueByActivityTypeDTO> leisureRevenue = result.stream()
            .filter(r -> "Leisure".equals(r.getActivityType())).findFirst();
        assertTrue(leisureRevenue.isPresent());
        assertEquals(800L, leisureRevenue.get().getTotalRevenue());

        verify(orderedQuantityDb, times(1)).findByYearAndMonth(year, month);
    }

    @Test
    void testGetPotentialRevenue_withYearOnly() {
        Integer year = 2023;
        List<OrderedQuantity> mockOrders = List.of(oqAdventure2);
        when(orderedQuantityDb.findByYear(year)).thenReturn(mockOrders);

        List<RevenueByActivityTypeDTO> result = statisticsService.getPotentialRevenue(year, null);
        
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Adventure", result.get(0).getActivityType());
        assertEquals(150L, result.get(0).getTotalRevenue());
        
        verify(orderedQuantityDb, times(1)).findByYear(year);
    }

    @Test
    void testGetPotentialRevenue_withNoFilters() {
        List<OrderedQuantity> mockOrders = List.of(oqAdventure1, oqAdventure2, oqLeisure1);
        when(orderedQuantityDb.findAll()).thenReturn(mockOrders);

        List<RevenueByActivityTypeDTO> result = statisticsService.getPotentialRevenue(null, null);

        assertNotNull(result);
        assertEquals(2, result.size());

        Optional<RevenueByActivityTypeDTO> adventureRevenue = result.stream()
            .filter(r -> "Adventure".equals(r.getActivityType())).findFirst();
        assertTrue(adventureRevenue.isPresent());
        assertEquals(350L, adventureRevenue.get().getTotalRevenue());

        Optional<RevenueByActivityTypeDTO> leisureRevenue = result.stream()
            .filter(r -> "Leisure".equals(r.getActivityType())).findFirst();
        assertTrue(leisureRevenue.isPresent());
        assertEquals(800L, leisureRevenue.get().getTotalRevenue());
        
        verify(orderedQuantityDb, times(1)).findAll();
    }
    
    @Test
    void testGetPotentialRevenue_whenNoOrdersFound() {
        when(orderedQuantityDb.findAll()).thenReturn(Collections.emptyList());

        List<RevenueByActivityTypeDTO> result = statisticsService.getPotentialRevenue(null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderedQuantityDb, times(1)).findAll();
    }
}
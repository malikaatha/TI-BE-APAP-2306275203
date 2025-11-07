package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdateOrderedQuantityRequestDTO;

import java.util.UUID;

public interface OrderedQuantityService {
    void addActivityToPlan(UUID planId, CreateOrderedQuantityRequestDTO dto);
    void updateOrderedQuantity(UUID orderedQuantityId, UpdateOrderedQuantityRequestDTO dto);
    void removeActivityFromPlan(UUID orderedQuantityId);
}
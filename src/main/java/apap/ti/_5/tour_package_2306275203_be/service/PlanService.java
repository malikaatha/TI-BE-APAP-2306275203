package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreatePlanRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdatePlanRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.PlanResponseDTO;

import java.util.UUID;

public interface PlanService {
    PlanResponseDTO createPlan(String packageId, CreatePlanRequestDTO dto);
    PlanResponseDTO getPlanById(UUID id);
    PlanResponseDTO updatePlan(UUID id, UpdatePlanRequestDTO dto);
    void deletePlan(UUID id);
}   
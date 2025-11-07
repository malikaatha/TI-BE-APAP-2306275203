package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreatePlanRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdatePlanRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.OrderedQuantityResponseDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.PlanResponseDTO;
import apap.ti._5.tour_package_2306275203_be.model.Plan;
import apap.ti._5.tour_package_2306275203_be.model.TourPackage;
import apap.ti._5.tour_package_2306275203_be.repository.PlanDb;
import apap.ti._5.tour_package_2306275203_be.repository.TourPackageDb;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlanServiceImpl implements PlanService {
    private final PlanDb planDb;

    private final TourPackageDb tourPackageDb;

    public PlanServiceImpl(PlanDb planDb, TourPackageDb tourPackageDb) {
        this.planDb = planDb;
        this.tourPackageDb = tourPackageDb;
    }

    @Override
    public PlanResponseDTO createPlan(String packageId, CreatePlanRequestDTO dto) {
        TourPackage tourPackage = tourPackageDb.findById(packageId)
                .orElseThrow(() -> new NoSuchElementException("Package not found"));

        if (!"Pending".equals(tourPackage.getStatus())) {
            throw new IllegalStateException("Plans can only be added to 'Pending' packages.");
        }

        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("Plan end date cannot be before start date.");
        }
        if (dto.getStartDate().isBefore(tourPackage.getStartDate())) {
            throw new IllegalArgumentException("Plan start date cannot be before package start date.");
        }
        if (dto.getEndDate().isAfter(tourPackage.getEndDate())) {
            throw new IllegalArgumentException("Plan end date cannot be after package end date.");
        }
        if ("Accommodation".equals(dto.getActivityType()) && !dto.getStartLocation().equals(dto.getEndLocation())) {
            throw new IllegalArgumentException("For Accommodation, start and end locations must be the same.");
        }

        Plan plan = new Plan();
        plan.setPlanName(dto.getPlanName());
        plan.setActivityType(dto.getActivityType());
        plan.setStartDate(dto.getStartDate());
        plan.setEndDate(dto.getEndDate());
        plan.setStartLocation(dto.getStartLocation());
        plan.setEndLocation(dto.getEndLocation());
        plan.setPrice(0L);
        plan.setStatus("Unfulfilled");
        plan.setTourPackage(tourPackage);
        
        Plan savedPlan = planDb.save(plan);
        return convertToResponseDTO(savedPlan);
    }

    @Override
    public PlanResponseDTO getPlanById(UUID id) {
        Plan plan = planDb.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Plan not found"));
        return convertToResponseDTO(plan);
    }

    @Override
    public PlanResponseDTO updatePlan(UUID id, UpdatePlanRequestDTO dto) {
        Plan plan = planDb.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Plan not found"));

        if (!"Pending".equals(plan.getTourPackage().getStatus())) {
            throw new IllegalStateException("Can only edit plans of 'Pending' packages.");
        }
        if (plan.getListOrderedQuantity() != null && !plan.getListOrderedQuantity().isEmpty()) {
            throw new IllegalStateException("Cannot edit a plan that already has ordered activities.");
        }

        plan.setPlanName(dto.getPlanName());
        plan.setStartDate(dto.getStartDate());
        plan.setEndDate(dto.getEndDate());
        plan.setStartLocation(dto.getStartLocation());
        plan.setEndLocation(dto.getEndLocation());

        Plan updatedPlan = planDb.save(plan);
        return convertToResponseDTO(updatedPlan);
    }

    @Override
    public void deletePlan(UUID id) {
        Plan plan = planDb.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Plan not found"));
        
        if (!"Pending".equals(plan.getTourPackage().getStatus())) {
            throw new IllegalStateException("Can only delete plans from 'Pending' packages.");
        }

        planDb.delete(plan);
    }
    
public static PlanResponseDTO convertToResponseDTO(Plan plan) {
    List<OrderedQuantityResponseDTO> orderedQuantities = new ArrayList<>();
    if (plan.getListOrderedQuantity() != null) {
        orderedQuantities = plan.getListOrderedQuantity().stream().map(oq -> 
            new OrderedQuantityResponseDTO(
                oq.getId(),
                oq.getOrderedQuota(),
                oq.getPrice(),
                oq.getActivity().getActivityName(),
                oq.getActivity().getId()
            )).collect(Collectors.toList());
    }

        return new PlanResponseDTO(
            plan.getId(),
            plan.getPlanName(),
            plan.getPrice(),
            plan.getActivityType(),
            plan.getStatus(),
            plan.getStartDate(),
            plan.getEndDate(),
            plan.getStartLocation(),
            plan.getEndLocation(),
            plan.getTourPackage().getId(),
            orderedQuantities
        );
    }
}
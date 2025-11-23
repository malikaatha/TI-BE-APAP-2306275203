package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.model.*;
import apap.ti._5.tour_package_2306275203_be.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class OrderedQuantityServiceImpl implements OrderedQuantityService {

    private final OrderedQuantityDb orderedQuantityDb;
    private final PlanDb planDb;
    private final ActivityDb activityDb;
    private final TourPackageDb tourPackageDb;

    public OrderedQuantityServiceImpl(OrderedQuantityDb orderedQuantityDb, PlanDb planDb, ActivityDb activityDb, TourPackageDb tourPackageDb) {
        this.orderedQuantityDb = orderedQuantityDb;
        this.planDb = planDb;
        this.activityDb = activityDb;
        this.tourPackageDb = tourPackageDb;
    }

    @Override
    public void addActivityToPlan(UUID planId, CreateOrderedQuantityRequestDTO dto) {
        Plan plan = planDb.findById(planId)
                .orElseThrow(() -> new NoSuchElementException("Plan not found"));
        
        Activity activity = activityDb.findById(dto.getActivityId())
                .orElseThrow(() -> new NoSuchElementException("Activity not found"));

        validateActivityRequirements(activity);
        validateActivityAddition(plan, activity, dto.getOrderedQuota(), null);

        OrderedQuantity oq = new OrderedQuantity();
        oq.setPlan(plan);
        oq.setActivity(activity);
        oq.setOrderedQuota(dto.getOrderedQuota());
        oq.setPrice(activity.getPrice());
        oq.setQuota(activity.getCapacity());
        oq.setStartDate(activity.getStartDate());
        oq.setEndDate(activity.getEndDate());

        orderedQuantityDb.save(oq);
        recalculatePlanAndUpdateStatus(plan);
    }
    
    @Override
    public void updateOrderedQuantity(UUID orderedQuantityId, UpdateOrderedQuantityRequestDTO dto) {
        OrderedQuantity oq = orderedQuantityDb.findById(orderedQuantityId)
                .orElseThrow(() -> new NoSuchElementException("Ordered item not found"));
        
        Plan plan = oq.getPlan();
        Activity activity = oq.getActivity();

        validateActivityAddition(plan, activity, dto.getOrderedQuota(), orderedQuantityId);

        oq.setOrderedQuota(dto.getOrderedQuota());
        orderedQuantityDb.save(oq);
        recalculatePlanAndUpdateStatus(plan);
    }
    
    @Override
    public void removeActivityFromPlan(UUID orderedQuantityId) {
        OrderedQuantity oq = orderedQuantityDb.findById(orderedQuantityId)
                .orElseThrow(() -> new NoSuchElementException("Ordered item not found"));
        
        Plan plan = oq.getPlan();

        if (!"Pending".equals(plan.getTourPackage().getStatus())) {
             throw new IllegalStateException("Cannot modify a processed package.");
        }

        orderedQuantityDb.delete(oq);
        orderedQuantityDb.flush(); 

        recalculatePlanAndUpdateStatus(plan);
    }

    private void validateActivityRequirements(Activity activity) {
        if (activity.getPrice() <= 0) {
            throw new IllegalArgumentException("Activity price must be greater than 0.");
        }
        if (activity.getCapacity() <= 0) {
            throw new IllegalArgumentException("Activity capacity must be greater than 0.");
        }
    }

    private void validateActivityAddition(Plan plan, Activity activity, int newOrderedQuota, UUID excludeOqId) {
        TourPackage tourPackage = plan.getTourPackage();
        
        if (!"Pending".equals(tourPackage.getStatus())) {
            throw new IllegalStateException("Cannot modify a processed package.");
        }
        
        if (!activity.getActivityType().equals(plan.getActivityType())) {
            throw new IllegalArgumentException("Activity type does not match plan's activity type.");
        }

        if (activity.getStartDate().isBefore(plan.getStartDate())) {
            throw new IllegalArgumentException("Activity start date cannot be before the plan's start date.");
        }
        if (activity.getEndDate().isAfter(plan.getEndDate())) {
            throw new IllegalArgumentException("Activity end date cannot be after the plan's end date.");
        }

        if (newOrderedQuota <= 0) {
            throw new IllegalArgumentException("Ordered quota must be greater than 0.");
        }

        if (newOrderedQuota > activity.getCapacity()) {
            throw new IllegalArgumentException("Ordered quantity exceeds activity capacity.");
        }

        int currentTotalQuotaInPlan = plan.getListOrderedQuantity().stream()
                .filter(oq -> !oq.getId().equals(excludeOqId)) 
                .mapToInt(OrderedQuantity::getOrderedQuota)
                .sum();

        if ((currentTotalQuotaInPlan + newOrderedQuota) > tourPackage.getQuota()) {
            throw new IllegalArgumentException("Total ordered quantity in plan exceeds package quota.");
        }
    }

    private void recalculatePlanAndUpdateStatus(Plan plan) {
        Plan freshPlan = planDb.findById(plan.getId()).orElseThrow();
        TourPackage tourPackage = freshPlan.getTourPackage();
        
        List<OrderedQuantity> orderedQuantitiesForPlan = orderedQuantityDb.findByPlan(freshPlan);

        long newPlanPrice = orderedQuantitiesForPlan.stream()
                .mapToLong(o -> o.getPrice() * o.getOrderedQuota())
                .sum();
        freshPlan.setPrice(newPlanPrice);

        int totalOrderedInPlan = orderedQuantitiesForPlan.stream()
                .mapToInt(OrderedQuantity::getOrderedQuota)
                .sum();

        if (totalOrderedInPlan == tourPackage.getQuota()) {
            freshPlan.setStatus("Fulfilled");
        } else {
            freshPlan.setStatus("Unfulfilled");
        }

        planDb.save(freshPlan);

        recalculatePackagePrice(tourPackage);
    }

    private void recalculatePackagePrice(TourPackage tourPackage) {
        long newPackagePrice = tourPackage.getListPlan().stream()
                .mapToLong(Plan::getPrice)
                .sum();
        tourPackage.setPrice(newPackagePrice);
        tourPackageDb.save(tourPackage);
    }
}
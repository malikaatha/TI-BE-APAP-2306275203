package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.model.*;
import apap.ti._5.tour_package_2306275203_be.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
public class OrderedQuantityServiceImpl implements OrderedQuantityService {

    @Autowired private OrderedQuantityDb orderedQuantityDb;
    @Autowired private PlanDb planDb;
    @Autowired private ActivityDb activityDb;
    @Autowired private TourPackageDb tourPackageDb;

    @Override
    public void addActivityToPlan(UUID planId, CreateOrderedQuantityRequestDTO dto) {
        Plan plan = planDb.findById(planId).orElseThrow(() -> new NoSuchElementException("Plan not found"));
        Activity activity = activityDb.findById(dto.getActivityId()).orElseThrow(() -> new NoSuchElementException("Activity not found"));
        TourPackage tourPackage = plan.getTourPackage();

        validateActivityAddition(plan, activity, dto.getOrderedQuota());

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
        OrderedQuantity oq = orderedQuantityDb.findById(orderedQuantityId).orElseThrow(() -> new NoSuchElementException("Ordered item not found"));
        Plan plan = oq.getPlan();

        validateActivityAddition(plan, oq.getActivity(), dto.getOrderedQuota());

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

    plan.getListOrderedQuantity().remove(oq);

    orderedQuantityDb.delete(oq);

    recalculatePlanAndUpdateStatus(plan);
}

private void validateActivityAddition(Plan plan, Activity activity, int newOrderedQuota) {
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

    if (newOrderedQuota > activity.getCapacity()) {
        throw new IllegalArgumentException("Ordered quantity exceeds activity capacity.");
    }

    int currentTotalQuotaInPlan = plan.getListOrderedQuantity().stream()
            .mapToInt(OrderedQuantity::getOrderedQuota)
            .sum();

    if ((currentTotalQuotaInPlan + newOrderedQuota) > tourPackage.getQuota()) {
        throw new IllegalArgumentException("Total ordered quantity in plan exceeds package quota.");
    }
}

    private void recalculatePlanAndUpdateStatus(Plan plan) {
        long newPlanPrice = plan.getListOrderedQuantity().stream()
                .mapToLong(o -> o.getPrice() * o.getOrderedQuota())
                .sum();
        plan.setPrice(newPlanPrice);

        int totalOrdered = plan.getListOrderedQuantity().stream().mapToInt(OrderedQuantity::getOrderedQuota).sum();
        if (totalOrdered == plan.getTourPackage().getQuota()) {
            plan.setStatus("Fulfilled");
        } else {
            plan.setStatus("Unfulfilled");
        }
        planDb.save(plan);

        recalculatePackagePrice(plan.getTourPackage());
    }

    private void recalculatePackagePrice(TourPackage tourPackage) {
        long newPackagePrice = tourPackage.getListPlan().stream()
                .mapToLong(Plan::getPrice)
                .sum();
        tourPackage.setPrice(newPackagePrice);
        tourPackageDb.save(tourPackage);
    }
}
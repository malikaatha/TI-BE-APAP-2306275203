package apap.ti._5.tour_package_2306275203_be.controller;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdateOrderedQuantityRequestDTO;
import apap.ti._5.tour_package_2306275203_be.service.OrderedQuantityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/ordered-activities")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderedQuantityController {
    private final OrderedQuantityService orderedQuantityService;

    public OrderedQuantityController(OrderedQuantityService orderedQuantityService) {
        this.orderedQuantityService = orderedQuantityService;
    }

    // Fitur 11: Add Ordered Activities to Plan
    @PostMapping("/create/plan/{planId}")
    public ResponseEntity<String> addActivityToPlan(@PathVariable("planId") UUID planId, @RequestBody CreateOrderedQuantityRequestDTO dto) {
        try {
            orderedQuantityService.addActivityToPlan(planId, dto);
            return ResponseEntity.ok("Activity added to plan successfully.");
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Fitur 12: Edit Ordered Quantity
    @PutMapping("/{id}/edit")
    public ResponseEntity<String> updateOrderedQuantity(@PathVariable("id") UUID id, @RequestBody UpdateOrderedQuantityRequestDTO dto) {
        try {
            orderedQuantityService.updateOrderedQuantity(id, dto);
            return ResponseEntity.ok("Ordered quantity updated successfully.");
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    // Fitur 13: Delete Ordered Quantity
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> removeActivityFromPlan(@PathVariable("id") UUID id) {
        try {
            orderedQuantityService.removeActivityFromPlan(id);
            return ResponseEntity.ok("Activity removed from plan successfully.");
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
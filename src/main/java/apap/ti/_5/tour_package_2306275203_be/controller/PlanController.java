package apap.ti._5.tour_package_2306275203_be.controller;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreatePlanRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdatePlanRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.PlanResponseDTO;
import apap.ti._5.tour_package_2306275203_be.service.PlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class PlanController {
    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping("/packages/{packageId}/plans/create")
    public ResponseEntity<?> createPlan(@PathVariable("packageId") String packageId, @RequestBody CreatePlanRequestDTO dto) {
        try {
            PlanResponseDTO response = planService.createPlan(packageId, dto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/plans/{id}")
    public ResponseEntity<?> getPlanById(@PathVariable("id") UUID id) {
        try {
            PlanResponseDTO response = planService.getPlanById(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Plan not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/plans/{id}/edit")
    public ResponseEntity<?> updatePlan(@PathVariable("id") UUID id, @RequestBody UpdatePlanRequestDTO dto) {
        try {
            PlanResponseDTO response = planService.updatePlan(id, dto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Plan not found", HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/plans/{id}/delete")
    public ResponseEntity<String> deletePlan(@PathVariable("id") UUID id) {
        try {
            planService.deletePlan(id);
            return new ResponseEntity<>("Plan deleted successfully.", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Plan not found", HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
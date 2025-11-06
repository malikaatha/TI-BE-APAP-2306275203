package apap.ti._5.tour_package_2306275203_be.controller;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreateTourPackageRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdateTourPackageRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.TourPackageResponseDTO;
import apap.ti._5.tour_package_2306275203_be.service.TourPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/package")
@CrossOrigin(origins = "http://localhost:5173")
public class TourPackageController {

    @Autowired
    private TourPackageService tourPackageService;

    // Fitur 4: Create Package
    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> createTourPackage(@RequestBody CreateTourPackageRequestDTO createTourPackageRequestDTO) {
        try {
            TourPackageResponseDTO responseDTO = tourPackageService.createTourPackage(createTourPackageRequestDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Fitur 2: Read All Packages
    @GetMapping("")
    public ResponseEntity<List<TourPackageResponseDTO>> getAllTourPackage() {
        List<TourPackageResponseDTO> listTourPackage = tourPackageService.getAllTourPackage();
        return new ResponseEntity<>(listTourPackage, HttpStatus.OK);
    }

    // Fitur 3: Detail Package
    @GetMapping("/{id}")
    public ResponseEntity<?> getTourPackageById(@PathVariable("id") String id) {
        try {
            TourPackageResponseDTO responseDTO = tourPackageService.getTourPackageById(id);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) {
             return new ResponseEntity<>("Package not found", HttpStatus.NOT_FOUND);
        }
    }

    // Fitur 6: Edit Package (Update)
    @PutMapping("/{id}/edit")
    public ResponseEntity<?> updateTourPackage(@PathVariable("id") String id, @RequestBody UpdateTourPackageRequestDTO updateDTO) {
        try {
            TourPackageResponseDTO responseDTO = tourPackageService.updateTourPackage(id, updateDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Package not found", HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Fitur 5: Delete Package (Soft)
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteTourPackage(@PathVariable("id") String id) {
        try {
            tourPackageService.deleteTourPackage(id);
            return new ResponseEntity<>("Package deleted successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Package not found", HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Fitur 7: Process Package
    @PutMapping("/{id}/process")
    public ResponseEntity<?> processPackage(@PathVariable("id") String id) {
        try {
            TourPackageResponseDTO responseDTO = tourPackageService.processPackage(id);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Package not found", HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
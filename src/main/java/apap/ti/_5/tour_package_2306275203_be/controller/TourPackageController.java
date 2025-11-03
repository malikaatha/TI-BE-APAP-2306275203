package apap.ti._5.tour_package_2306275203_be.controller;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreateTourPackageRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.TourPackageResponseDTO;
import apap.ti._5.tour_package_2306275203_be.service.TourPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/package")
public class TourPackageController {

    @Autowired
    private TourPackageService tourPackageService;

    @PostMapping("")
    public ResponseEntity<TourPackageResponseDTO> createTourPackage(@RequestBody CreateTourPackageRequestDTO createTourPackageRequestDTO) {
        TourPackageResponseDTO responseDTO = tourPackageService.createTourPackage(createTourPackageRequestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<List<TourPackageResponseDTO>> getAllTourPackage() {
        List<TourPackageResponseDTO> listTourPackage = tourPackageService.getAllTourPackage();
        return new ResponseEntity<>(listTourPackage, HttpStatus.OK);
    }

    @GetMapping("/{id_package}")
    public ResponseEntity<?> getTourPackageById(@PathVariable("id_package") String id_package) {
        TourPackageResponseDTO responseDTO = tourPackageService.getTourPackageById(id_package);
        if (responseDTO == null) {
            return new ResponseEntity<>("Package not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
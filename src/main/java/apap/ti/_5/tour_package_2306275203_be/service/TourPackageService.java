package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreateTourPackageRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdateTourPackageRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.PlanResponseDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.TourPackageResponseDTO;

import java.util.List;

public interface TourPackageService {
    TourPackageResponseDTO createTourPackage(CreateTourPackageRequestDTO dto);
    List<TourPackageResponseDTO> getAllTourPackage();
    TourPackageResponseDTO getTourPackageById(String packageId);
    TourPackageResponseDTO updateTourPackage(String id, UpdateTourPackageRequestDTO dto);
    void deleteTourPackage(String id);
    TourPackageResponseDTO processPackage(String id);
    public List<PlanResponseDTO> getAllPlansByPackageId(String packageId);
}
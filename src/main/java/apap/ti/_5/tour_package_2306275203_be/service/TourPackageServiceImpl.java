package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreateTourPackageRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.TourPackageResponseDTO;
import apap.ti._5.tour_package_2306275203_be.model.TourPackage;
import apap.ti._5.tour_package_2306275203_be.repository.TourPackageDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TourPackageServiceImpl implements TourPackageService {

    @Autowired
    private TourPackageDb tourPackageDb;

    @Override
    public TourPackageResponseDTO createTourPackage(CreateTourPackageRequestDTO dto) {
        TourPackage tourPackage = TourPackage.builder()
                .id(UUID.randomUUID().toString())
                .userId(dto.getUserId())
                .packageName(dto.getPackageName())
                .quota(dto.getQuota())
                .price(dto.getPrice())
                .status(dto.getStatus())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
        
        TourPackage savedPackage = tourPackageDb.save(tourPackage);
        return convertToResponseDTO(savedPackage);
    }

    @Override
    public List<TourPackageResponseDTO> getAllTourPackage() {
        List<TourPackage> allTourPackages = tourPackageDb.findAll();
        return allTourPackages.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TourPackageResponseDTO getTourPackageById(String id) {
        return tourPackageDb.findById(id)
                .map(this::convertToResponseDTO)
                .orElse(null);
    }

    private TourPackageResponseDTO convertToResponseDTO(TourPackage tourPackage) {
        return TourPackageResponseDTO.builder()
                .id(tourPackage.getId())
                .userId(tourPackage.getUserId())
                .packageName(tourPackage.getPackageName())
                .quota(tourPackage.getQuota())
                .price(tourPackage.getPrice())
                .status(tourPackage.getStatus())
                .startDate(tourPackage.getStartDate())
                .endDate(tourPackage.getEndDate())
                .build();
    }
}
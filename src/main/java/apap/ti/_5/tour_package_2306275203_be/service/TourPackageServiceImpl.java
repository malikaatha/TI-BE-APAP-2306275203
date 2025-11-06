package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreateTourPackageRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdateTourPackageRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.TourPackageResponseDTO;
import apap.ti._5.tour_package_2306275203_be.model.Activity;
import apap.ti._5.tour_package_2306275203_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306275203_be.model.Plan;
import apap.ti._5.tour_package_2306275203_be.model.TourPackage;
import apap.ti._5.tour_package_2306275203_be.repository.ActivityDb;
import apap.ti._5.tour_package_2306275203_be.repository.TourPackageDb;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class TourPackageServiceImpl implements TourPackageService {

    @Autowired
    private TourPackageDb tourPackageDb;

    @Autowired
    private ActivityDb activityDb;

    @Override
    public TourPackageResponseDTO createTourPackage(CreateTourPackageRequestDTO dto) {
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }

        long count = tourPackageDb.countByUserId(dto.getUserId());
        String packageId = String.format("PACK-%s-%03d", dto.getUserId(), count + 1);

        TourPackage tourPackage = TourPackage.builder()
                .id(packageId)
                .userId(dto.getUserId())
                .packageName(dto.getPackageName())
                .quota(dto.getQuota())
                .price(0L) // Harga awal adalah 0
                .status("Pending")
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
        
        TourPackage savedPackage = tourPackageDb.save(tourPackage);
        return convertToResponseDTO(savedPackage);
    }

    @Override
    public List<TourPackageResponseDTO> getAllTourPackage() {
        return tourPackageDb.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TourPackageResponseDTO getTourPackageById(String id) {
        TourPackage tourPackage = tourPackageDb.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Package not found"));
        return convertToResponseDTO(tourPackage);
    }

    @Override
    public TourPackageResponseDTO updateTourPackage(String id, UpdateTourPackageRequestDTO dto) {
        TourPackage tourPackage = tourPackageDb.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Package not found"));

        if (!"Pending".equals(tourPackage.getStatus())) {
            throw new IllegalStateException("Only packages with 'Pending' status can be edited.");
        }

        if (tourPackage.getListPlan() != null && !tourPackage.getListPlan().isEmpty()) {
            throw new IllegalStateException("Cannot edit package that already has plans.");
        }

        tourPackage.setPackageName(dto.getPackageName());
        tourPackage.setQuota(dto.getQuota());
        tourPackage.setStartDate(dto.getStartDate());
        tourPackage.setEndDate(dto.getEndDate());

        tourPackageDb.save(tourPackage);
        return convertToResponseDTO(tourPackage);
    }

    @Override
    public void deleteTourPackage(String id) {
        TourPackage tourPackage = tourPackageDb.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Package not found"));

        if (!"Pending".equals(tourPackage.getStatus())) {
            throw new IllegalStateException("Only packages with 'Pending' status can be deleted.");
        }
        
        tourPackageDb.delete(tourPackage);
    }

    @Override
    public TourPackageResponseDTO processPackage(String id) {
        TourPackage tourPackage = tourPackageDb.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Package not found"));

        if (!"Pending".equals(tourPackage.getStatus())) {
            throw new IllegalStateException("Only 'Pending' packages can be processed.");
        }

        if (tourPackage.getListPlan() == null || tourPackage.getListPlan().isEmpty()) {
            throw new IllegalStateException("Package cannot be processed without any plans.");
        }
        
        for (Plan plan : tourPackage.getListPlan()) {
            if (!"Fulfilled".equals(plan.getStatus())) {
                throw new IllegalStateException("All plans must be 'Fulfilled' to process the package.");
            }
        }

        for (Plan plan : tourPackage.getListPlan()) {
            for (OrderedQuantity oq : plan.getListOrderedQuantity()) {
                Activity activity = oq.getActivity();
                if (activity.getCapacity() < oq.getOrderedQuota()) {
                     throw new IllegalStateException("Not enough capacity for activity: " + activity.getActivityName());
                }
                activity.setCapacity(activity.getCapacity() - oq.getOrderedQuota());
                activityDb.save(activity);
            }
        }

        tourPackage.setStatus("Processed");
        tourPackageDb.save(tourPackage);
        return convertToResponseDTO(tourPackage);
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
package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.request.CreateTourPackageRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.request.UpdateTourPackageRequestDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.PlanResponseDTO;
import apap.ti._5.tour_package_2306275203_be.dto.response.TourPackageResponseDTO;
import apap.ti._5.tour_package_2306275203_be.model.Activity;
import apap.ti._5.tour_package_2306275203_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306275203_be.model.Plan;
import apap.ti._5.tour_package_2306275203_be.model.TourPackage;
import apap.ti._5.tour_package_2306275203_be.repository.ActivityDb;
import apap.ti._5.tour_package_2306275203_be.repository.TourPackageDb;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class TourPackageServiceImpl implements TourPackageService {
    private final TourPackageDb tourPackageDb;

    private final ActivityDb activityDb;

    public TourPackageServiceImpl(TourPackageDb tourPackageDb, ActivityDb activityDb) {
        this.tourPackageDb = tourPackageDb;
        this.activityDb = activityDb;
    }

        @Override
        public TourPackageResponseDTO createTourPackage(CreateTourPackageRequestDTO dto) {
            if (dto.getStartDate().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Start date cannot be in the past.");
            }

            if (dto.getEndDate().isBefore(dto.getStartDate())) {
                throw new IllegalArgumentException("End date cannot be before start date.");
            }

            if (dto.getQuota() <= 0) {
                throw new IllegalArgumentException("Quota must be greater than 0.");
            }

            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String prefix = "PKG-" + dateStr + "-";

            var latestPackage = tourPackageDb.findTopByIdStartingWithOrderByIdDesc(prefix);

            int nextSequence = 1;
            if (latestPackage.isPresent()) {
                String lastId = latestPackage.get().getId();
                try {
                    String lastSequenceStr = lastId.substring(lastId.length() - 3);
                    nextSequence = Integer.parseInt(lastSequenceStr) + 1;
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    nextSequence = 1;
                }
            }

            String packageId = String.format("%s%03d", prefix, nextSequence);

            TourPackage tourPackage = TourPackage.builder()
                    .id(packageId)
                    .userId(dto.getUserId())
                    .packageName(dto.getPackageName())
                    .quota(dto.getQuota())
                    .price(0L) 
                    .status("Pending")
                    .startDate(dto.getStartDate())
                    .endDate(dto.getEndDate())
                    .build();
            
            TourPackage savedPackage = tourPackageDb.save(tourPackage);
            return convertToResponseDTO(savedPackage);
        }

        String packageId = String.format("PACK-%s-%03d", dto.getUserId(), nextSequence);
        
        TourPackage tourPackage = TourPackage.builder()
                .id(packageId)
                .userId(dto.getUserId())
                .packageName(dto.getPackageName())
                .quota(dto.getQuota())
                .price(0L) 
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
                .sorted(Comparator.comparing(TourPackage::getStartDate)) 
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
        List<PlanResponseDTO> planDTOs = new ArrayList<>();
        if (tourPackage.getListPlan() != null) {
            planDTOs = tourPackage.getListPlan().stream()
                    .map(PlanServiceImpl::convertToResponseDTO)
                    .collect(Collectors.toList());
        }

        return TourPackageResponseDTO.builder()
                .id(tourPackage.getId())
                .userId(tourPackage.getUserId())
                .packageName(tourPackage.getPackageName())
                .quota(tourPackage.getQuota())
                .price(tourPackage.getPrice())
                .status(tourPackage.getStatus())
                .startDate(tourPackage.getStartDate())
                .endDate(tourPackage.getEndDate())
                .listPlan(planDTOs)
                .build();
    }
}
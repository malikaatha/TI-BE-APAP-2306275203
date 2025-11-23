package apap.ti._5.tour_package_2306275203_be.repository;

import apap.ti._5.tour_package_2306275203_be.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityDb extends JpaRepository<Activity, String> {
    List<Activity> findByActivityTypeAndStartLocationAndEndLocationAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
    String activityType, String startLocation, String endLocation, java.time.LocalDateTime planStartDate, java.time.LocalDateTime planEndDate);

    Optional<Activity> findTopByIdStartingWithOrderByIdDesc(String prefix);

    @Query(value = "SELECT * FROM activity WHERE id LIKE :prefix% ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Optional<Activity> findLatestIdIncludingDeleted(@Param("prefix") String prefix);

        @Query(value = """
        SELECT * FROM activity a 
        WHERE 
            -- 1. Filter isDeleted (Jika includeDeleted=true, tampilkan semua. Jika false, hanya yang aktif)
            ((:includeDeleted = true) OR (a.is_deleted = false))
            
            -- 2. Filter Activity Type (Optional)
            AND (:activityType IS NULL OR a.activity_type = :activityType)
            
            -- 3. Filter Location (Optional)
            AND (:startLocation IS NULL OR a.start_location = :startLocation)
            AND (:endLocation IS NULL OR a.end_location = :endLocation)
            
            -- 4. Filter Date (Optional)
            AND (CAST(:startDate AS timestamp) IS NULL OR a.start_date >= :startDate)
            AND (CAST(:endDate AS timestamp) IS NULL OR a.end_date <= :endDate)
            
            -- 5. Search Activity Name OR Item (Case Insensitive)
            AND (:search IS NULL OR 
                LOWER(a.activity_name) LIKE LOWER(CONCAT('%', :search, '%')) OR 
                LOWER(a.activity_item) LIKE LOWER(CONCAT('%', :search, '%')))
        
        -- 6. Default Sorting: startDate Ascending
        ORDER BY a.start_date ASC
        """, nativeQuery = true)
    List<Activity> findAllActivitiesWithFilters(
            @Param("includeDeleted") boolean includeDeleted,
            @Param("activityType") String activityType,
            @Param("startLocation") String startLocation,
            @Param("endLocation") String endLocation,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("search") String search
    );
}
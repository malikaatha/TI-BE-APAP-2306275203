package apap.ti._5.tour_package_2306275203_be.repository;

import apap.ti._5.tour_package_2306275203_be.model.TourPackage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TourPackageDb extends JpaRepository<TourPackage, String> {
        @Query("SELECT count(p) FROM TourPackage p WHERE p.userId = :userId AND p.isDeleted = false")
        long countByUserIdAndIsDeletedFalse(@Param("userId") String userId);

        @Query("SELECT p.id FROM TourPackage p WHERE p.userId = :userId ORDER BY p.id DESC")
        List<String> findLatestIdByUserId(@Param("userId") String userId);
}
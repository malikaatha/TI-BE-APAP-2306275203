package apap.ti._5.tour_package_2306275203_be.repository;

import apap.ti._5.tour_package_2306275203_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306275203_be.model.Plan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderedQuantityDb extends JpaRepository<OrderedQuantity, UUID> {
    @Query(value = "SELECT * FROM ordered_quantity oq WHERE EXTRACT(YEAR FROM oq.start_date) = :year AND EXTRACT(MONTH FROM oq.start_date) = :month", nativeQuery = true)
    List<OrderedQuantity> findByYearAndMonth(int year, int month);

    @Query(value = "SELECT * FROM ordered_quantity oq WHERE EXTRACT(YEAR FROM oq.start_date) = :year", nativeQuery = true)
    List<OrderedQuantity> findByYear(int year);

    List<OrderedQuantity> findByPlan(Plan plan);

    List<OrderedQuantity> findByStartDateBetween(LocalDateTime start, LocalDateTime end);


}
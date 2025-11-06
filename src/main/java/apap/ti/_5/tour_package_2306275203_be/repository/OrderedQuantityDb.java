package apap.ti._5.tour_package_2306275203_be.repository;

import apap.ti._5.tour_package_2306275203_be.model.OrderedQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderedQuantityDb extends JpaRepository<OrderedQuantity, UUID> {
    @Query("SELECT oq FROM OrderedQuantity oq WHERE FUNCTION('YEAR', oq.startDate) = :year AND FUNCTION('MONTH', oq.startDate) = :month")
    List<OrderedQuantity> findByYearAndMonth(int year, int month);

    @Query("SELECT oq FROM OrderedQuantity oq WHERE FUNCTION('YEAR', oq.startDate) = :year")
    List<OrderedQuantity> findByYear(int year);
}
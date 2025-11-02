package apap.ti._5.tour_package_2306275203_be.repository;

import apap.ti._5.tour_package_2306275203_be.model.OrderedQuantity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderedQuantityDb extends JpaRepository<OrderedQuantity, UUID> {
}
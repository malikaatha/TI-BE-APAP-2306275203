package apap.ti._5.tour_package_2306275203_be.repository;

import apap.ti._5.tour_package_2306275203_be.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityDb extends JpaRepository<Activity, String> {
}
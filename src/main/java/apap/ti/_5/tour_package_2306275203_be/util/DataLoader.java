package apap.ti._5.tour_package_2306275203_be.util;

import apap.ti._5.tour_package_2306275203_be.model.Activity;
import apap.ti._5.tour_package_2306275203_be.repository.ActivityDb;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ActivityDb activityDb;

    @Override
    public void run(String... args) throws Exception {
        if (activityDb.count() == 0) {
            Faker faker = new Faker();
            
            for (int i = 0; i < 5; i++) {
                Activity activity = new Activity();
                activity.setId("FLIGHT-" + faker.random().hex(5));
                activity.setActivityName("Flight from " + faker.aviation().airport() + " to " + faker.aviation().airport());
                activity.setActivityItem(faker.aviation().aircraft());
                activity.setCapacity(faker.number().numberBetween(30, 100));
                activity.setPrice(faker.number().numberBetween(1, 5) * 500000L);
                activity.setActivityType("Flight");

                LocalDateTime startDate = faker.date().future(365, TimeUnit.DAYS, new java.util.Date(125,0,1)).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                activity.setStartDate(startDate);
                activity.setEndDate(startDate.plusHours(faker.number().numberBetween(1, 5)));
                
                activity.setStartLocation("DKI Jakarta (Provinsi)"); 
                activity.setEndLocation("Bali (Provinsi)");
                
                activityDb.save(activity);
            }
        }
    }
}
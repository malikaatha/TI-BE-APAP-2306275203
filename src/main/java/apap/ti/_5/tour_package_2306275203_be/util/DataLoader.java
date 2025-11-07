package apap.ti._5.tour_package_2306275203_be.util;

import apap.ti._5.tour_package_2306275203_be.model.Activity;
import apap.ti._5.tour_package_2306275203_be.repository.ActivityDb;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ActivityDb activityDb;

    @Override
    public void run(String... args) throws Exception {
        if (activityDb.count() == 0) {
            System.out.println("Database Activity kosong, mengisi dengan data dummy...");
            Faker faker = new Faker();

            // 1. Flight di Awal November
            Activity flightNov1 = new Activity();
            flightNov1.setId("FLIGHT-NOV-001");
            flightNov1.setActivityName("Garuda Indonesia: Jakarta - Bali");
            flightNov1.setActivityItem("GA-408 Business Class");
            flightNov1.setCapacity(40);
            flightNov1.setPrice(2500000L);
            flightNov1.setActivityType("Flight");
            LocalDateTime flightNov1Start = LocalDateTime.of(2025, 11, 5, 9, 0); // 5 November 2025
            flightNov1.setStartDate(flightNov1Start);
            flightNov1.setEndDate(flightNov1Start.plusHours(2));
            flightNov1.setStartLocation("DKI Jakarta (Provinsi)");
            flightNov1.setEndLocation("Bali (Provinsi)");
            activityDb.save(flightNov1);

            // 2. Flight di Pertengahan November
            Activity flightNov2 = new Activity();
            flightNov2.setId("FLIGHT-NOV-002");
            flightNov2.setActivityName("Citilink: Jakarta - Bali");
            flightNov2.setActivityItem("QG-682 Economy");
            flightNov2.setCapacity(120);
            flightNov2.setPrice(950000L);
            flightNov2.setActivityType("Flight");
            LocalDateTime flightNov2Start = LocalDateTime.of(2025, 11, 15, 14, 30); // 15 November 2025
            flightNov2.setStartDate(flightNov2Start);
            flightNov2.setEndDate(flightNov2Start.plusHours(2));
            flightNov2.setStartLocation("DKI Jakarta (Provinsi)");
            flightNov2.setEndLocation("Bali (Provinsi)");
            activityDb.save(flightNov2);

            // 3. Akomodasi di Bali
            Activity hotelBali = new Activity();
            hotelBali.setId("HOTEL-BALI-001");
            hotelBali.setActivityName("Menginap di The Anvaya Beach Resort");
            hotelBali.setActivityItem("Deluxe Room with Breakfast");
            hotelBali.setCapacity(30);
            hotelBali.setPrice(1800000L);
            hotelBali.setActivityType("Accommodation");
            // Akomodasi biasanya rentangnya lebih lebar
            LocalDateTime hotelStart = LocalDateTime.of(2025, 11, 5, 14, 0);
            hotelBali.setStartDate(hotelStart);
            hotelBali.setEndDate(LocalDateTime.of(2025, 11, 10, 12, 0));
            hotelBali.setStartLocation("Bali (Provinsi)");
            hotelBali.setEndLocation("Bali (Provinsi)"); // Untuk akomodasi, lokasi sama
            activityDb.save(hotelBali);

            // 4. Sewa Mobil di Bali
            Activity carBali = new Activity();
            carBali.setId("CAR-BALI-001");
            carBali.setActivityName("Sewa Toyota Avanza (Lepas Kunci)");
            carBali.setActivityItem("7-seater MPV");
            carBali.setCapacity(15);
            carBali.setPrice(350000L);
            carBali.setActivityType("Vehicle Rental");
            LocalDateTime carStart = LocalDateTime.of(2025, 11, 5, 0, 0);
            carBali.setStartDate(carStart);
            carBali.setEndDate(LocalDateTime.of(2025, 11, 12, 23, 59));
            carBali.setStartLocation("Bali (Provinsi)");
            carBali.setEndLocation("Bali (Provinsi)");
            activityDb.save(carBali);
            
            // 5. Flight di Bulan Lain (untuk tes filter)
            Activity flightDec = new Activity();
            flightDec.setId("FLIGHT-DEC-001");
            flightDec.setActivityName("AirAsia: Jakarta - Bali");
            flightDec.setActivityItem("QZ-7510 Economy");
            flightDec.setCapacity(150);
            flightDec.setPrice(800000L);
            flightDec.setActivityType("Flight");
            LocalDateTime flightDecStart = LocalDateTime.of(2025, 12, 20, 7, 0); // 20 Desember 2025
            flightDec.setStartDate(flightDecStart);
            flightDec.setEndDate(flightDecStart.plusHours(2));
            flightDec.setStartLocation("DKI Jakarta (Provinsi)");
            flightDec.setEndLocation("Bali (Provinsi)");
            activityDb.save(flightDec);

            System.out.println("Data dummy berhasil dibuat.");
        }
    }
}
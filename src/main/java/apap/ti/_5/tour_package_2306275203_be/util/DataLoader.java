package apap.ti._5.tour_package_2306275203_be.util;

import apap.ti._5.tour_package_2306275203_be.model.Activity;
import apap.ti._5.tour_package_2306275203_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306275203_be.model.Plan;
import apap.ti._5.tour_package_2306275203_be.model.TourPackage;
import apap.ti._5.tour_package_2306275203_be.repository.ActivityDb;
import apap.ti._5.tour_package_2306275203_be.repository.OrderedQuantityDb;
import apap.ti._5.tour_package_2306275203_be.repository.PlanDb;
import apap.ti._5.tour_package_2306275203_be.repository.TourPackageDb;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DataLoader implements CommandLineRunner {

    private final ActivityDb activityDb;
    private final TourPackageDb tourPackageDb;
    private final PlanDb planDb;
    private final OrderedQuantityDb orderedQuantityDb;

    public DataLoader(ActivityDb activityDb, TourPackageDb tourPackageDb, PlanDb planDb, OrderedQuantityDb orderedQuantityDb) {
        this.activityDb = activityDb;
        this.tourPackageDb = tourPackageDb;
        this.planDb = planDb;
        this.orderedQuantityDb = orderedQuantityDb;
    }

    @Override
    public void run(String... args) throws Exception {
        if (activityDb.count() == 0) {
            System.out.println("Database Activity kosong, mengisi dengan data dummy...");

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

            Activity hotelBali = new Activity();
            hotelBali.setId("HOTEL-BALI-001");
            hotelBali.setActivityName("Menginap di The Anvaya Beach Resort");
            hotelBali.setActivityItem("Deluxe Room with Breakfast");
            hotelBali.setCapacity(30);
            hotelBali.setPrice(1800000L);
            hotelBali.setActivityType("Accommodation");

            LocalDateTime hotelStart = LocalDateTime.of(2025, 11, 5, 14, 0);
            hotelBali.setStartDate(hotelStart);
            hotelBali.setEndDate(LocalDateTime.of(2025, 11, 10, 12, 0));
            hotelBali.setStartLocation("Bali (Provinsi)");
            hotelBali.setEndLocation("Bali (Provinsi)"); // Untuk akomodasi, lokasi sama
            activityDb.save(hotelBali);

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

            System.out.println("Data dummy Activity berhasil dibuat.");
        }

        if (tourPackageDb.count() == 0) {
            System.out.println("Database Package kosong, mengisi dengan data dummy package/plan/orderedQuantity...");

            TourPackage pkg = TourPackage.builder()
                    .id("PACK-U1-001")
                    .userId("U1")
                    .packageName("Bali Getaway")
                    .quota(10)
                    .price(0L)
                    .status("Pending")
                    .startDate(LocalDateTime.of(2025, 11, 5, 0, 0))
                    .endDate(LocalDateTime.of(2025, 11, 12, 23, 59))
                    .build();

            Plan planFlight = new Plan();
            planFlight.setPlanName("Flight to Bali");
            planFlight.setActivityType("Flight");
            planFlight.setStartDate(LocalDateTime.of(2025, 11, 5, 9, 0));
            planFlight.setEndDate(LocalDateTime.of(2025, 11, 5, 11, 0));
            planFlight.setStartLocation("DKI Jakarta (Provinsi)");
            planFlight.setEndLocation("Bali (Provinsi)");
            planFlight.setPrice(0L);
            planFlight.setStatus("Unfulfilled");
            planFlight.setTourPackage(pkg);

            Plan planHotel = new Plan();
            planHotel.setPlanName("Akomodasi di Bali");
            planHotel.setActivityType("Accommodation");
            planHotel.setStartDate(LocalDateTime.of(2025, 11, 5, 14, 0));
            planHotel.setEndDate(LocalDateTime.of(2025, 11, 10, 12, 0));
            planHotel.setStartLocation("Bali (Provinsi)");
            planHotel.setEndLocation("Bali (Provinsi)");
            planHotel.setPrice(0L);
            planHotel.setStatus("Unfulfilled");
            planHotel.setTourPackage(pkg);

            List<Plan> plans = new ArrayList<>();
            plans.add(planFlight);
            plans.add(planHotel);
            pkg.setListPlan(plans);

            TourPackage savedPkg = tourPackageDb.save(pkg);

            Optional<Activity> optFlight = activityDb.findById("FLIGHT-NOV-001");
            Optional<Activity> optHotel = activityDb.findById("HOTEL-BALI-001");

            if (optFlight.isPresent() && optHotel.isPresent()) {
                Plan persistedFlightPlan = savedPkg.getListPlan().stream()
                        .filter(p -> "Flight to Bali".equals(p.getPlanName()))
                        .findFirst().orElse(null);

                Plan persistedHotelPlan = savedPkg.getListPlan().stream()
                        .filter(p -> "Akomodasi di Bali".equals(p.getPlanName()))
                        .findFirst().orElse(null);

                if (persistedFlightPlan != null) {
                    Activity a = optFlight.get();
                    OrderedQuantity oq = new OrderedQuantity();
                    oq.setPlan(persistedFlightPlan);
                    oq.setActivity(a);
                    oq.setOrderedQuota(2);
                    oq.setPrice(a.getPrice());
                    oq.setQuota(a.getCapacity());
                    oq.setStartDate(a.getStartDate());
                    oq.setEndDate(a.getEndDate());
                    orderedQuantityDb.save(oq);
                }

                if (persistedHotelPlan != null) {
                    Activity a = optHotel.get();
                    OrderedQuantity oq2 = new OrderedQuantity();
                    oq2.setPlan(persistedHotelPlan);
                    oq2.setActivity(a);
                    oq2.setOrderedQuota(2);
                    oq2.setPrice(a.getPrice());
                    oq2.setQuota(a.getCapacity());
                    oq2.setStartDate(a.getStartDate());
                    oq2.setEndDate(a.getEndDate());
                    orderedQuantityDb.save(oq2);
                }

                for (Plan p : savedPkg.getListPlan()) {
                    List<OrderedQuantity> oqsForThisPlan = orderedQuantityDb.findByPlan(p);
                    
                    long planPrice = 0L;
                    if (oqsForThisPlan != null) {
                        for (OrderedQuantity oq : oqsForThisPlan) {
                            planPrice += (oq.getPrice() * oq.getOrderedQuota());
                        }
                    }
                    p.setPrice(planPrice);
                    planDb.save(p);
                }

                long pkgPrice = savedPkg.getListPlan().stream().mapToLong(Plan::getPrice).sum();
                savedPkg.setPrice(pkgPrice);
                tourPackageDb.save(savedPkg);
            }

            System.out.println("Data dummy Package/Plan/OrderedQuantity berhasil dibuat.");
        }
    }
}
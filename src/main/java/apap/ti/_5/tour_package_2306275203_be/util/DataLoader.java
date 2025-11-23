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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import java.util.Random;

@Component
@Profile("!production")
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
    Faker faker = new Faker(Locale.forLanguageTag("id-ID"));
        Random rand = new Random();

        System.out.println("Clearing existing Activity/Package/Plan/OrderedQuantity data and reseeding with Faker... ");
        orderedQuantityDb.deleteAll();
        planDb.deleteAll();
        tourPackageDb.deleteAll();
        activityDb.deleteAll();

        System.out.println("Database Activity kosong, mengisi dengan data dummy menggunakan Faker...");

            String[] locations = new String[]{
                "DKI Jakarta (Provinsi)",
                "Bali (Provinsi)",
                "Jawa Barat (Provinsi)",
                "Yogyakarta (Provinsi)",
                "Kota Denpasar" 
            };

            String[] activityTypes = new String[]{"Flight", "Accommodation", "Vehicle Rental"};

            int totalActivities = 30; 
            for (int i = 1; i <= totalActivities; i++) {
                String type = activityTypes[rand.nextInt(activityTypes.length)];
                Activity act = new Activity();

                if ("Flight".equals(type)) {
                    act.setId(String.format("FLIGHT-%03d", i));
                    String origin = locations[rand.nextInt(locations.length)];
                    String dest;
                    do { dest = locations[rand.nextInt(locations.length)]; } while (dest.equals(origin));
                    act.setActivityName(faker.company().name() + ": " + origin + " - " + dest);
                    act.setActivityItem(faker.bothify("FL-###") + " " + (rand.nextBoolean() ? "Economy" : "Business"));
                    act.setCapacity(50 + rand.nextInt(151));
                    act.setPrice(500_000L + rand.nextInt(4_500_001));
                    act.setActivityType("Flight");
                    LocalDateTime start = LocalDateTime.now().plusDays(1 + rand.nextInt(90)).withHour(6 + rand.nextInt(15)).withMinute(rand.nextBoolean() ? 0 : 30);
                    act.setStartDate(start);
                    act.setEndDate(start.plusHours(1 + rand.nextInt(6)));
                    act.setStartLocation(origin);
                    act.setEndLocation(dest);
                } else if ("Accommodation".equals(type)) {
                    act.setId(String.format("HOTEL-%03d", i));
                    act.setActivityName(faker.company().name() + " Hotel");
                    act.setActivityItem((rand.nextBoolean() ? "Deluxe Room" : "Standard Room") + " with Breakfast");
                    act.setCapacity(5 + rand.nextInt(46));
                    act.setPrice(200_000L + rand.nextInt(1_800_001));
                    act.setActivityType("Accommodation");
                    String loc = locations[rand.nextInt(locations.length)];
                    LocalDateTime start = LocalDateTime.now().plusDays(1 + rand.nextInt(90)).withHour(14).withMinute(0);
                    act.setStartDate(start);
                    act.setEndDate(start.plusDays(1 + rand.nextInt(10)).withHour(12).withMinute(0));
                    act.setStartLocation(loc);
                    act.setEndLocation(loc);
                } else { // Vehicle Rental
                    act.setId(String.format("CAR-%03d", i));
                    String model = faker.aviation().aircraft();
                    act.setActivityName("Sewa " + model);
                    act.setActivityItem(model + " - " + faker.bothify("Model-###"));
                    act.setCapacity(1 + rand.nextInt(20));
                    act.setPrice(100_000L + rand.nextInt(900_001));
                    act.setActivityType("Vehicle Rental");
                    String loc = locations[rand.nextInt(locations.length)];
                    LocalDateTime start = LocalDateTime.now().plusDays(1 + rand.nextInt(90)).withHour(8 + rand.nextInt(10)).withMinute(0);
                    act.setStartDate(start);
                    act.setEndDate(start.plusDays(1 + rand.nextInt(7)).withHour(20).withMinute(0));
                    act.setStartLocation(loc);
                    act.setEndLocation(loc);
                }

                if (act.getStartDate() == null) act.setStartDate(LocalDateTime.now().plusDays(1));
                if (act.getEndDate() == null) act.setEndDate(act.getStartDate().plusHours(2));
                if (act.getActivityItem() == null) act.setActivityItem("Standard Item");
                if (act.getActivityName() == null) act.setActivityName("Unnamed Activity");

                activityDb.save(act);
            }

            System.out.println("Data dummy Activity berhasil dibuat dengan Faker.");

        System.out.println("Database Package kosong, mengisi dengan banyak data dummy package/plan/orderedQuantity...");

            String[] sampleUsers = new String[]{"user001", "user002", "user003", "user004"};

            List<Activity> allActivities = activityDb.findAll();
            if (allActivities.isEmpty()) {
                System.out.println("No activities available to attach to packages. Skipping package creation.");
            } else {
                for (String user : sampleUsers) {
                    int userPackageCount = 1 + rand.nextInt(3);
                    for (int p = 0; p < userPackageCount; p++) {
                        List<String> latestIds = tourPackageDb.findLatestIdByUserId(user);
                        int nextSequence = 1;

                        if (!latestIds.isEmpty()) {
                            String lastId = latestIds.get(0);
                            try {
                                String lastSequenceStr = lastId.substring(lastId.lastIndexOf('-') + 1);
                                nextSequence = Integer.parseInt(lastSequenceStr) + 1;
                            } catch (Exception e) {
                                nextSequence = latestIds.size() + 1;
                            }
                        } else {
                            nextSequence = p + 1; 
                        }
                        
                        long currentCountForUserInLoop = tourPackageDb.countByUserIdAndIsDeletedFalse(user);
                        String packageId = String.format("PACK-%s-%03d", user, currentCountForUserInLoop + 1);

                        LocalDateTime pkgStart = LocalDateTime.now().plusDays(1 + rand.nextInt(60)).withHour(0).withMinute(0);
                        LocalDateTime pkgEnd = pkgStart.plusDays(2 + rand.nextInt(12)).withHour(23).withMinute(59);

                        String[] packageTypes = {"Adventure Tour", "City Tour", "Cultural Experience", "Beach Vacation", "Luxury Getaway", "Family Holiday"};
                        String destination = locations[rand.nextInt(locations.length)].replace(" (Provinsi)", "");
                        String packageType = packageTypes[rand.nextInt(packageTypes.length)];
                        String duration = (pkgEnd.getDayOfMonth() - pkgStart.getDayOfMonth() + 1) + "D" + 
                                       ((pkgEnd.getDayOfMonth() - pkgStart.getDayOfMonth()) + 1 - 1) + "N";
                        
                        TourPackage pkg = TourPackage.builder()
                                .id(packageId)
                                .userId(user)
                                .packageName(destination + " " + packageType + " " + duration)
                                .quota(2 + rand.nextInt(20))
                                .price(0L)
                                .status("Pending")
                                .startDate(pkgStart)
                                .endDate(pkgEnd)
                                .build();

                        TourPackage savedPkg = tourPackageDb.save(pkg);

                        int planCount = 1 + rand.nextInt(4); 
                        List<Plan> createdPlans = new ArrayList<>();

                        for (int pi = 0; pi < planCount; pi++) {
                            String activityType = activityTypes[rand.nextInt(activityTypes.length)];

                            Plan plan = new Plan();
                            plan.setPlanName(faker.lorem().sentence(2).replace(".", ""));
                            plan.setActivityType(activityType);

                            long daysBetween = ChronoUnit.DAYS.between(pkgStart, pkgEnd);
                            int startDayOffset = 0;
                            if (daysBetween > 0) startDayOffset = rand.nextInt((int) daysBetween + 1);
                            LocalDateTime planStart = pkgStart.plusDays(startDayOffset)
                                    .withHour(8 + rand.nextInt(10))
                                    .withMinute(rand.nextBoolean() ? 0 : 30)
                                    .withSecond(0)
                                    .withNano(0);

                            long hoursRemaining = ChronoUnit.HOURS.between(planStart, pkgEnd);
                            if (hoursRemaining < 1) {
                                planStart = pkgStart.withHour(9).withMinute(0).withSecond(0).withNano(0);
                                hoursRemaining = Math.max(1, ChronoUnit.HOURS.between(planStart, pkgEnd));
                            }
                            int planDurationHours = 1 + rand.nextInt((int) Math.max(1, Math.min(hoursRemaining, 72L)));
                            LocalDateTime planEnd = planStart.plusHours(planDurationHours);
                            if (planEnd.isAfter(pkgEnd)) planEnd = pkgEnd;

                            plan.setStartDate(planStart);
                            plan.setEndDate(planEnd);

                            String startLoc = locations[rand.nextInt(locations.length)];
                            String endLoc = startLoc;
                            if (activityType.equals("Flight")) {
                                do {
                                    endLoc = locations[rand.nextInt(locations.length)];
                                } while (endLoc.equals(startLoc));
                            }
                            plan.setStartLocation(startLoc);
                            plan.setEndLocation(endLoc);

                            plan.setPrice(0L);
                            plan.setStatus("Unfulfilled");
                            plan.setTourPackage(savedPkg);

                            Plan savedPlan = planDb.save(plan);
                            createdPlans.add(savedPlan);

                            Activity matchingActivity = new Activity();
                            long activityIndex = activityDb.count() + 1;
                            String prefix = activityType.equals("Flight") ? "FLIGHT" : activityType.equals("Accommodation") ? "HOTEL" : "CAR";
                            matchingActivity.setId(String.format(prefix + "-PLAN-%03d", activityIndex));
                            matchingActivity.setActivityType(activityType);

                            long possibleStartHours = Math.max(0, ChronoUnit.HOURS.between(planStart, planEnd) - 1);
                            long startOffsetHours = possibleStartHours > 0 ? rand.nextInt((int) possibleStartHours + 1) : 0;
                            LocalDateTime activityStart = planStart.plusHours(startOffsetHours).withSecond(0).withNano(0);

                            int activityDurationHours = 1 + rand.nextInt(4); // 1-4 hours typical
                            LocalDateTime activityEnd = activityStart.plusHours(activityDurationHours);
                            if (activityEnd.isAfter(planEnd)) activityEnd = planEnd;

                            matchingActivity.setStartDate(activityStart);
                            matchingActivity.setEndDate(activityEnd);
                            matchingActivity.setStartLocation(startLoc);
                            matchingActivity.setEndLocation(endLoc);

                            if (activityType.equals("Flight")) {
                                matchingActivity.setActivityName(faker.company().name() + ": " + startLoc + " - " + endLoc);
                                matchingActivity.setActivityItem(faker.bothify("FL-###") + " " + (rand.nextBoolean() ? "Economy" : "Business"));
                                matchingActivity.setCapacity(50 + rand.nextInt(151));
                                matchingActivity.setPrice(500_000L + rand.nextInt(4_500_001));
                            } else if (activityType.equals("Accommodation")) {
                                matchingActivity.setActivityName(faker.company().name() + " Hotel");
                                matchingActivity.setActivityItem((rand.nextBoolean() ? "Deluxe Room" : "Standard Room") + " with Breakfast");
                                matchingActivity.setCapacity(5 + rand.nextInt(46));
                                matchingActivity.setPrice(200_000L + rand.nextInt(1_800_001));
                            } else {
                                String model = faker.aviation().aircraft();
                                matchingActivity.setActivityName("Sewa " + model);
                                matchingActivity.setActivityItem(model + " - " + faker.bothify("Model-###"));
                                matchingActivity.setCapacity(1 + rand.nextInt(20));
                                matchingActivity.setPrice(100_000L + rand.nextInt(900_001));
                            }

                            Activity savedMatching = activityDb.save(matchingActivity);
                            allActivities.add(savedMatching);

                            int oqCount = 1 + rand.nextInt(3);
                            for (int oqi = 0; oqi < oqCount; oqi++) {
                                Activity actChoice = allActivities.get(rand.nextInt(allActivities.size()));
                                OrderedQuantity oq = new OrderedQuantity();
                                oq.setPlan(savedPlan);
                                oq.setActivity(actChoice);
                                int maxOrder = Math.max(1, Math.min(5, actChoice.getCapacity()));
                                int qty = 1 + rand.nextInt(maxOrder);
                                oq.setOrderedQuota(qty);
                                oq.setPrice(actChoice.getPrice());
                                oq.setQuota(actChoice.getCapacity());
                                oq.setStartDate(actChoice.getStartDate());
                                oq.setEndDate(actChoice.getEndDate());
                                orderedQuantityDb.save(oq);
                            }

                            List<OrderedQuantity> oqs = orderedQuantityDb.findByPlan(savedPlan);
                            long planPrice = 0L;
                            if (oqs != null) {
                                for (OrderedQuantity oq : oqs) {
                                    planPrice += (oq.getPrice() * oq.getOrderedQuota());
                                }
                            }
                            savedPlan.setPrice(planPrice);
                            planDb.save(savedPlan);
                        }

                        List<Plan> pkgPlans = planDb.findByTourPackage(savedPkg);
                        long pkgPrice = 0L;
                        if (pkgPlans != null) {
                            for (Plan pp : pkgPlans) {
                                pkgPrice += (pp.getPrice() != null ? pp.getPrice() : 0L);
                            }
                        }
                        savedPkg.setPrice(pkgPrice);
                        tourPackageDb.save(savedPkg);
                    }
                }

                System.out.println("Data dummy Package/Plan/OrderedQuantity berhasil dibuat dengan Faker.");
            }
    }
}
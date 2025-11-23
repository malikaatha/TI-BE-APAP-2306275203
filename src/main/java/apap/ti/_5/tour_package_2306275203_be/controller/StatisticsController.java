package apap.ti._5.tour_package_2306275203_be.controller;

import apap.ti._5.tour_package_2306275203_be.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
@CrossOrigin(origins = "http://localhost:5173")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping
    public ResponseEntity<?> getRevenue(
            @RequestParam Integer year,
            @RequestParam(required = false) Integer month
    ) {
        return ResponseEntity.ok(statisticsService.getRevenueStatistics(year, month));
    }

    @GetMapping("/revenue/yearly/{year}")
    public ResponseEntity<?> getYearlyRevenue(@PathVariable Integer year) {
        return ResponseEntity.ok(statisticsService.getRevenueStatistics(year, null));
    }

    @GetMapping("/revenue/monthly/{year}/{month}")
    public ResponseEntity<?> getMonthlyRevenue(@PathVariable Integer year, @PathVariable Integer month) {
        return ResponseEntity.ok(statisticsService.getRevenueStatistics(year, month));
    }
}
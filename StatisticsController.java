package apap.ti._5.tour_package_2306275203_be.controller;

import apap.ti._5.tour_package_2306275203_be.dto.response.RevenueByActivityTypeDTO;
import apap.ti._5.tour_package_2306275203_be.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/statistics")
@CrossOrigin(origins = "http://localhost:5173")
public class StatisticsController {

    private final StatisticsService statisticsService;

    // Fitur 15: Statistik Potensial Revenue
    @GetMapping("")
    public ResponseEntity<List<RevenueByActivityTypeDTO>> getPotentialRevenue(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month
    ) {
        List<RevenueByActivityTypeDTO> revenueData = statisticsService.getPotentialRevenue(year, month);
        return ResponseEntity.ok(revenueData);
    }
}
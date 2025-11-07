package apap.ti._5.tour_package_2306275203_be.service;

import apap.ti._5.tour_package_2306275203_be.dto.response.RevenueByActivityTypeDTO;
import apap.ti._5.tour_package_2306275203_be.model.OrderedQuantity;
import apap.ti._5.tour_package_2306275203_be.repository.OrderedQuantityDb;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final OrderedQuantityDb orderedQuantityDb;

    public StatisticsServiceImpl(OrderedQuantityDb orderedQuantityDb) {
        this.orderedQuantityDb = orderedQuantityDb;
    }

    @Override
    public List<RevenueByActivityTypeDTO> getPotentialRevenue(Integer year, Integer month) {
        List<OrderedQuantity> relevantOrders;

        if (year != null && month != null && month > 0) {
            relevantOrders = orderedQuantityDb.findByYearAndMonth(year, month);
        } else if (year != null) {
            relevantOrders = orderedQuantityDb.findByYear(year);
        } else {
            // Jika tidak ada filter, ambil semua
            relevantOrders = orderedQuantityDb.findAll();
        }

        Map<String, Long> revenueMap = relevantOrders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getActivity().getActivityType(), // Kelompokkan berdasarkan activityType
                        Collectors.summingLong(order -> order.getPrice() * order.getOrderedQuota()) // Jumlahkan (harga * kuota)
                ));

        return revenueMap.entrySet().stream()
                .map(entry -> new RevenueByActivityTypeDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
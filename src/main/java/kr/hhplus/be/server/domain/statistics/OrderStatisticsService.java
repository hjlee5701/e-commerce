package kr.hhplus.be.server.domain.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class OrderStatisticsService {

    private final OrderStatisticsRepository orderStatisticsRepository;
    private final static int POPULAR_PERIOD = 3;
    private final static int TOP_RANK = 5;


    public List<OrderStatisticsInfo.Popular> popular() {
        Pageable pageable = PageRequest.of(0, TOP_RANK);
        LocalDate now = LocalDateTime.now().toLocalDate().plusDays(1);

        Page<PopularProductsProjection> popularPage = orderStatisticsRepository.findPopularProductsForDateRange(now.minusDays(POPULAR_PERIOD), now, pageable);

        List<PopularProductsProjection> populars = popularPage.getContent();
        return IntStream.range(0, populars.size())
                .mapToObj(i -> OrderStatisticsInfo.Popular.of(i + 1, populars.get(i)))
                .toList();
    }


    public void aggregate(OrderStatisticsCommand.Aggregate command) {

        Map<Long, Integer> soldProduct = command.toSoldProductMap();
        LocalDate startDate = command.getStartDate();
        LocalDate endDate = command.getEndDate();

        List<OrderStatistics> orderStatistics =
                orderStatisticsRepository.getByProductIdsAndDate(startDate, endDate, soldProduct.keySet());

        Map<Long, OrderStatistics> statisticsMap = orderStatistics.stream()
                .collect(Collectors.toMap(
                        stat -> stat.getProduct().getId(),
                        Function.identity()
                ));

        for (Map.Entry<Long, Integer> entry : soldProduct.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            OrderStatistics statistics = statisticsMap.get(productId);

            if (statistics != null) {
                statistics.aggregateQuantity(quantity);
            } else {
                OrderStatistics newStat = OrderStatistics.create(productId, quantity, command.getEndDate());
                orderStatisticsRepository.save(newStat);
            }
        }
    }

}

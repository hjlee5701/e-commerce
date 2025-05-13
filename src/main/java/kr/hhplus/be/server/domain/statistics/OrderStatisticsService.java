package kr.hhplus.be.server.domain.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class OrderStatisticsService {

    private final OrderStatisticsRepository orderStatisticsRepository;

    @Cacheable(
            value = "Ranking",
            key = "'Ranking:' + #command.startDate + ':' + #command.endDate + ':' + #command.count"
    )
    public List<OrderStatisticsInfo.Popular> popular(OrderStatisticsCommand.Popular command) {
        Pageable pageable = PageRequest.of(0, command.getCount());

        Page<PopularProductsProjection> popularPage = orderStatisticsRepository.findPopularProductsForDateRange(command.getStartDate(), command.getEndDate(), pageable);

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

//    @Cacheable(
//            value = "PopularProductIds",
//            key = "'PopularProductIds:' + #command.startDate + ':' + #command.endDate + ':' + #command.count"
//    )
    public List<Long> popularProductIds(OrderStatisticsCommand.Popular command) {
        // 인기 상품의 ID 만 조회
        Pageable pageable = PageRequest.of(0, command.getCount());
        Page<Long> popularPage
                = orderStatisticsRepository.findPopularProductIdsForDateRange(
                        command.getStartDate(), command.getEndDate(), pageable);
        return popularPage.getContent();
    }
}
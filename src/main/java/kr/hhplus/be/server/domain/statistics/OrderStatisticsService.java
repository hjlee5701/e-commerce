package kr.hhplus.be.server.domain.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class OrderStatisticsService {

    private final OrderStatisticsRepository orderStatisticsRepository;
    private final static int POPULAR_PERIOD = 3;
    private final static int TOP_RANK = 5;


    public List<OrderStatisticsInfo.Popular> popular() {
        Pageable pageable = PageRequest.of(0, POPULAR_PERIOD);

        Page<PopularProductsProjection> popularPage = orderStatisticsRepository.findPopularProductsForDateRange(TOP_RANK, pageable);

        List<PopularProductsProjection> populars = popularPage.getContent();
        return IntStream.range(0, populars.size())
                .mapToObj(i -> OrderStatisticsInfo.Popular.of(i + 1, populars.get(i)))
                .toList();
    }

}

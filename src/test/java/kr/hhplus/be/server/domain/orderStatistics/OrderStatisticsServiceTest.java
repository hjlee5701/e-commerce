package kr.hhplus.be.server.domain.orderStatistics;

import kr.hhplus.be.server.domain.statistics.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.common.FixtureTestSupport.FIXED_NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderStatisticsServiceTest {

    @Mock
    private OrderStatisticsRepository orderStatisticsRepository;

    @InjectMocks
    private OrderStatisticsService orderStatisticsService;

    private final int TOP_RANK = 5;
    private final static int POPULAR_PERIOD = 3;


    @Test
    @DisplayName("인기 상품 조회 - 순위 매핑과 리턴 결과 검증")
    void 인기_상품_조회_정상_동작_검증() {
        // given
        List<PopularProductsProjection> projections = List.of(
                new PopularProductsProjection(1L, "상품A", BigDecimal.valueOf(100), 10L),
                new PopularProductsProjection(2L, "상품B", BigDecimal.valueOf(80), 10L),
                new PopularProductsProjection(3L, "상품C", BigDecimal.valueOf(50), 10L)
        );

        Page<PopularProductsProjection> projectionPage = new PageImpl<>(projections);

        given(orderStatisticsRepository.findPopularProductsForDateRange(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .willReturn(projectionPage);

        // when
        List<OrderStatisticsInfo.Popular> result = orderStatisticsService.popular();

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getRank()).isEqualTo(1);
        assertThat(result.get(1).getRank()).isEqualTo(2);
        assertThat(result.get(2).getRank()).isEqualTo(3);

        verify(orderStatisticsRepository, times(1))
                .findPopularProductsForDateRange(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    @DisplayName("인기 상품 조회 - Pageable 조건 까지 검증")
    void 인기_상품_조회_Pageable_정상_전달_검증() {
        // given
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        List<PopularProductsProjection> projections = List.of(
                new PopularProductsProjection(1L, "상품A", BigDecimal.valueOf(100), 10L),
                new PopularProductsProjection(2L, "상품B", BigDecimal.valueOf(80), 10L),
                new PopularProductsProjection(3L, "상품C", BigDecimal.valueOf(80), 10L),
                new PopularProductsProjection(4L, "상품D", BigDecimal.valueOf(80), 10L),
                new PopularProductsProjection(5L, "상품E", BigDecimal.valueOf(80), 10L)
        );

        Page<PopularProductsProjection> page = new PageImpl<>(projections);

        given(orderStatisticsRepository.findPopularProductsForDateRange(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .willReturn(page);

        // when
        List<OrderStatisticsInfo.Popular> result = orderStatisticsService.popular();

        // then
        verify(orderStatisticsRepository).findPopularProductsForDateRange(any(LocalDateTime.class), any(LocalDateTime.class), captor.capture());

        Pageable captured = captor.getValue();
        assertThat(result.size() == TOP_RANK).isTrue();
        assertThat(captured.getPageNumber()).isEqualTo(0);
        assertThat(captured.getPageSize()).isEqualTo(TOP_RANK);
        assertThat(captured.getSort().isUnsorted()).isTrue();
    }


    @Test
    @DisplayName("조회한 날짜에 상품 통계 정보가 없다면, 판매 상품에 대한 통계를 생성한다.")
    void 주문_통계에_새로운_상품_통계_추가() {

        // given
        List<OrderStatisticsCommand.AggregateItem> itemsCommand = List.of(
                new OrderStatisticsCommand.AggregateItem(1L, 30),
                new OrderStatisticsCommand.AggregateItem(2L, 30)
        );

        OrderStatisticsCommand.Aggregate command
                = new OrderStatisticsCommand.Aggregate(FIXED_NOW, itemsCommand);

        LocalDateTime startDate = command.getStatisticsAt().toLocalDate().atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(1);

        given(orderStatisticsRepository.getByProductIdsAndDate(startDate, endDate, command.toSoldProductMap().keySet()))
                .willReturn(List.of());

        // when
        orderStatisticsService.aggregate(command);

        // then
        verify(orderStatisticsRepository, times(2)).save(any(OrderStatistics.class));
    }


    @Test
    @DisplayName("조회한 날짜에 동일한 상품 통계 정보가 있다면, 판매 수량을 업데이트 한다.")
    void 두_상품_통계가_업데이트_되었는지_검증() {
        // given
        LocalDateTime now = FIXED_NOW;

        OrderStatisticsCommand.Aggregate command = new OrderStatisticsCommand.Aggregate(now, List.of(
                new OrderStatisticsCommand.AggregateItem(1L, 10),
                new OrderStatisticsCommand.AggregateItem(2L, 20)
        ));

        OrderStatistics stat1 = spy(OrderStatistics.create(1L, 100, now));
        OrderStatistics stat2 = spy(OrderStatistics.create(2L, 200, now));

        LocalDateTime startDate = command.getStatisticsAt().toLocalDate().atStartOfDay();
        LocalDateTime endDate = startDate.plusDays(1);

        given(orderStatisticsRepository.getByProductIdsAndDate(startDate, endDate, command.toSoldProductMap().keySet()))
                .willReturn(List.of(stat1, stat2));

        // when
        orderStatisticsService.aggregate(command);

        // then
        verify(stat1, times(1)).aggregateQuantity(10);
        verify(stat2, times(1)).aggregateQuantity(20);
        verify(orderStatisticsRepository, never()).save(any(OrderStatistics.class));
    }
}

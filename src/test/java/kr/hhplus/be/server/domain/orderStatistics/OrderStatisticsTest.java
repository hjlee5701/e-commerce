package kr.hhplus.be.server.domain.orderStatistics;

import kr.hhplus.be.server.shared.exception.ECommerceException;
import kr.hhplus.be.server.domain.statistics.OrderStatistics;
import kr.hhplus.be.server.shared.code.OrderStatisticsError;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

public class OrderStatisticsTest {

    @Test
    @DisplayName("주문 통계 데이터를 생성한다.")
    void 주문_통계_생() {
        // given
        Long productId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // when
        OrderStatistics statistics = OrderStatistics.create(
                productId,
                100,
                now
        );
        // then
        assertEquals(productId, statistics.getProduct().getId());
        assertEquals(now, statistics.getStatisticsAt());
    }


    @DisplayName("주문 통계 상품 추가시 판매 수량이 0 또는 음수일 경우 예외 발생한다.")
    @ParameterizedTest(name = "판매 수량이 {0} 이하면 예외 발생한다.")
    @ValueSource(ints = {0, -1})
    void 주문_통계의_판매량_누적_실패(int soldQuantity) {
        // given
        Long productId = 1L;
        LocalDateTime now = LocalDateTime.now();
        OrderStatistics statistics = OrderStatistics.create(
                productId,
                100,
                now
        );

        // when & then
        assertThatThrownBy(() -> statistics.aggregateQuantity(soldQuantity))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(OrderStatisticsError.INVALID_SOLD_QUANTITY.getMessage());
    }
}
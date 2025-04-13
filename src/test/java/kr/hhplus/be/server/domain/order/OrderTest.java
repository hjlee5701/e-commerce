package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.ECommerceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OrderTest {

    @Test
    @DisplayName("결제 완료 시 최종 금액으로 변경 되고 상태가 PAID 로 변경된다.")
    void 결제완료시_금액이_반영되고_상태가_PAID로_변경() {
        // given
        Order order = new OrderFixture().createWithStatus(OrderStatus.PENDING);

        BigDecimal finalAmount = BigDecimal.valueOf(10000);

        // when
        order.completePayment(finalAmount);

        // then
        assertThat(order.getTotalAmount()).isEqualByComparingTo(finalAmount);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }


    @Test
    @DisplayName("최종 주문 금액이 양수라면 totalAmount 가 해당 금액으로 변경된다.")
    void 최종_주문_금액이_양수이면_totalAmount가_업데이트된다() {
        // given
        Order order = new OrderFixture().create();
        BigDecimal amount = BigDecimal.valueOf(10000);

        // when
        order.updateFinalAmount(amount);

        // then
        assertThat(order.getTotalAmount()).isEqualByComparingTo(amount);
    }

    @Test
    @DisplayName("최종 주문 금액이 음수라면 totalAmount 가 0으로 변경된다.")
    void 최종_주문_금액이_음수이면_totalAmount가_0으로_변경() {
        // given
        Order order = new OrderFixture().create();
        BigDecimal amount = BigDecimal.valueOf(-100);

        // when
        order.updateFinalAmount(amount);

        // then
        assertThat(order.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("PAID 로 주문 상태 변경 요청 시, 이전 상태가 PENDING 이라면 변경 성공한다.")
    void 상태가_PENDING이면_PAID로_변경() {
        // given
        Order order = new OrderFixture().createWithStatus(OrderStatus.PENDING);

        // when
        order.markAsPaid();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("PAID 로 주문 상태 변경 요청 시, 이전 상태가 PENDING 이 아니라면 예외 발생한다.")
    void 상태가_PENDING이_아니면_예외가_발생한다() {
        // given
        Order order = new OrderFixture().createWithStatus(OrderStatus.CANCELLED);

        // when & then
        assertThatThrownBy(order::markAsPaid)
                .isInstanceOf(ECommerceException.class)
                .hasMessageContaining(OrderStatus.CANCELLED.name());
    }




}

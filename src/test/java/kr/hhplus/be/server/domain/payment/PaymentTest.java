package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.order.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PaymentTest {
    @Test
    @DisplayName("결제 상태가 PENDING이면 COMPELTED로 변경되고 주문 상태도 도 확정된다")
    void 결제완료로_변경_후_주문_확정() {
        // given
        Order mockOrder = mock(Order.class); // 주문은 테스트하지 않음
        Payment payment = new PaymentFixture().withOrderAndStatus(mockOrder,PaymentStatus.PENDING);

        // when
        payment.markAsPaid();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        verify(mockOrder).markAsPaid();
    }

    @ParameterizedTest
    @ValueSource(strings = {"COMPLETED", "CANCELED", "REFUNDED", "FAILED"})
    void 결제상태가_PENDING이_아니면_예외가_발생한다(String statusName) {
        // given
        PaymentStatus status = PaymentStatus.valueOf(statusName);
        Payment payment = new PaymentFixture().withOrderAndStatus(mock(Order.class), status);

        // when & then
        assertThatThrownBy(payment::markAsPaid)
                .isInstanceOf(NotPendingPaymentException.class)
                .hasMessageContaining(status.name());
    }


}

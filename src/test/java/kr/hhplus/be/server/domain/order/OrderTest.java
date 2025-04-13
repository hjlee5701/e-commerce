package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.OrderErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderTest {


    @Test
    @DisplayName("memberId 가 null 인 경우, 주문 생성에 실패한다.")
    void memberId_널_은_주문_생성_실패() {
        // given
        Long memberId = null;

        // when & then
        assertThatThrownBy(() -> Order.create(memberId))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("주문 생성시 totalAmount 는 0원으로 설정된다.")
    void 주문_생성시_총금액은_설정값은_0원() {
        // given & when
        Order order = Order.create(ANY_MEMBER_ID);

        // then
        assertEquals(order.getTotalAmount(), BigDecimal.ZERO);
    }

    @Test
    @DisplayName("주문 생성시 주문 상태는 PENDING 으로 설정된다.")
    void 주문_생성시_주문_상태는_PENDING() {
        // given & when
        Order order = Order.create(ANY_MEMBER_ID);

        // then
        assertEquals(order.getStatus(), OrderStatus.PENDING);
    }


    @DisplayName("주문 상품 추가시 수량이 0 또는 음수일 경우 예외 발생한다.")
    @ParameterizedTest(name = "주문 수량이 {0} 이하면 예외 발생한다.")
    @ValueSource(ints = {0, -1})
    void 주문_수량_0이하로_예외(int orderQuantity) {
        // given
        List<OrderCommand.ItemCreate> products = List.of(
                new OrderCommand.ItemCreate(1L, "상품", BigDecimal.TEN, 10)
        );
        Map<Long, Integer> orderProductMap = Map.of(
                1L, orderQuantity
        );
        Order order = new OrderFixture().create();

        // when & then
        assertThatThrownBy(() -> order.addItems(products, orderProductMap))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(OrderErrorCode.INVALID_ORDER_QUANTITY.getMessage());
    }


    @Test
    @DisplayName("주문 상품 추가시 수량이 양수일 경우, 주문 아이템 생성에 성공한다.")
    void 주문_수량_양수로_생성_성공() {
        // given
        List<OrderCommand.ItemCreate> products = List.of(
                new OrderCommand.ItemCreate(1L, "상품A", BigDecimal.TEN, 100),
                new OrderCommand.ItemCreate(2L, "상품B", BigDecimal.TEN, 100)
        );
        Map<Long, Integer> orderProductMap = Map.of(
                1L, 10, 2L, 20
        );
        Order order = new OrderFixture().create();

        // when
        order.addItems(products, orderProductMap);

        // then
        assertEquals(order.getOrderItems().size(), 2);
    }

    @Test
    @DisplayName("주문의 총 금액 계산시 주문 상품의 totalPrice 의 누적합이 적용된다.")
    void 주문의_총_주문액은_누적합() {
        // given
        BigDecimal priceA = BigDecimal.valueOf(100);
        BigDecimal priceB = BigDecimal.valueOf(200);

        OrderItem itemA = new OrderItemFixture().withTotalPrice(priceA);
        OrderItem itemB = new OrderItemFixture().withTotalPrice(priceB);

        Order order = new OrderFixture().withItems(List.of(itemA, itemB));

        // when
        order.calculateTotalAmount();

        // then
        assertEquals(priceA.add(priceB), order.getTotalAmount());
    }


    @Test
    @DisplayName("결제 완료될 경우, 최종 금액으로 변경 되고 상태가 PAID 로 변경된다.")
    void 결제완료시_금액이_반영되고_상태가_PAID로_변경() {
        // given
        Order order = new OrderFixture().withStatus(OrderStatus.PENDING);

        BigDecimal finalAmount = BigDecimal.valueOf(10000);

        // when
        order.applyPayment(finalAmount);

        // then
        assertThat(order.getTotalAmount()).isEqualByComparingTo(finalAmount);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }


    @Test
    @DisplayName("결제 완료될 경우, 최종 주문 금액이 양수라면 totalAmount 가 해당 금액으로 변경된다.")
    void 최종_주문_금액이_양수이면_totalAmount가_업데이트된다() {
        // given
        Order order = new OrderFixture().create();
        BigDecimal amount = BigDecimal.valueOf(10000);

        // when
        order.applyPayment(amount);

        // then
        assertThat(order.getTotalAmount()).isEqualByComparingTo(amount);
    }

    @Test
    @DisplayName("결제 완료될 경우, 최종 주문 금액이 음수라면 totalAmount 가 0으로 변경된다.")
    void 최종_주문_금액이_음수이면_totalAmount가_0으로_변경() {
        // given
        Order order = new OrderFixture().create();
        BigDecimal amount = BigDecimal.valueOf(-100);

        // when
        order.applyPayment(amount);

        // then
        assertThat(order.getTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("결제 완료될 경우, 이전 상태가 PENDING 이라면 PAID 로 변경된다.")
    void 상태가_PENDING이면_PAID로_변경() {
        // given
        Order order = new OrderFixture().withStatus(OrderStatus.PENDING);
        BigDecimal amount = BigDecimal.valueOf(100);

        // when
        order.applyPayment(amount);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("결제 완료될 경우, 이전 상태가 PENDING 이 아니라면 예외 발생한다.")
    void 상태가_PENDING이_아니면_예외가_발생한다() {
        // given
        Order order = new OrderFixture().withStatus(OrderStatus.CANCELLED);
        BigDecimal amount = BigDecimal.valueOf(100);

        // when & then
        assertThatThrownBy(() -> order.applyPayment(amount))
                .isInstanceOf(ECommerceException.class)
                .hasMessageContaining(OrderStatus.CANCELLED.name());
    }




}

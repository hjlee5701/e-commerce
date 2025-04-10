package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;

public class OrderFactoryTest {
    @DisplayName("새롭게 주문 생성시 PENDING 상태로 설정된다.")
    @Test
    void 주문_생성시_대기_상태() {
        // given
        OrderCommand.Create command = new OrderCommand.Create(ANY_MEMBER, null, BigDecimal.ZERO);

        // when
        Order order = OrderFactory.create(command);

        // then
        assertThat(order.getStatus()).isEqualByComparingTo(OrderStatus.PENDING);
    }


    @DisplayName("요청 값에 따라 주문 아이템 생성에 성공한다.")
    @Test
    void 주문_아이템_생성_성공() {
        // given
        ProductFixture fixture = new ProductFixture();
        Product product = fixture.create();

        int orderQuantity = 10;
        OrderCommand.ItemCreate command = new OrderCommand.ItemCreate(product, product.getPrice(), orderQuantity);
        Order order = new OrderFixture().create();

        // when
        OrderItem orderItem = OrderFactory.createItem(order, command);

        // then
        assertThat(orderItem.getOrder()).isEqualTo(order);
        assertThat(orderItem.getProduct()).isEqualTo(product);
        assertThat(orderItem.getPrice()).isEqualByComparingTo(product.getPrice());
        assertThat(orderItem.getQuantity()).isEqualTo(orderQuantity);
        assertThat(orderItem.getTitle()).isEqualTo(product.getTitle());
    }

}

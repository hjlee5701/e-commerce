package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.common.FixtureReflectionUtils;
import kr.hhplus.be.server.common.TestFixture;
import kr.hhplus.be.server.domain.product.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
public class OrderItemFixture implements TestFixture<OrderItem> {

    private Long id = 1L;
    private String title ="주문 상품";
    private Order order = new Order();
    private Product product = new Product();
    private BigDecimal unitPrice = BigDecimal.ZERO;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private int quantity = 0;

    @Override
    public OrderItem create() {
        OrderItem entity = new OrderItem();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public OrderItem withTotalPrice(BigDecimal totalPrice) {
        OrderItem entity = new OrderItem();
        this.totalPrice = totalPrice;
        FixtureReflectionUtils.reflect(entity, this);
        return entity;

    }
}

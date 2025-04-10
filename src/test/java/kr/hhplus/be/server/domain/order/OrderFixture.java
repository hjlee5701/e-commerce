package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.common.FixtureReflectionUtils;
import kr.hhplus.be.server.common.TestFixture;
import kr.hhplus.be.server.domain.member.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class OrderFixture implements TestFixture<Order> {

    private final Long id = 1L;
    private Member member = new Member();

    private BigDecimal totalAmount = BigDecimal.ZERO;

    private OrderStatus status = OrderStatus.PENDING;

    private LocalDateTime orderedAt = LocalDateTime.now();

    @Override
    public Order create() {
        Order entity = new Order();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public Order createWithStatus(OrderStatus orderStatus) {
        Order entity = new Order();
        this.status = orderStatus;
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
}

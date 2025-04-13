package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.common.FixtureReflectionUtils;
import kr.hhplus.be.server.common.TestFixture;
import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.order.Order;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Getter
@Setter
@Accessors(chain = true)
public class PaymentFixture implements TestFixture<Payment> {

    private final Long id = 1L;
    private Order order = new Order();

    private Member member = new Member();

    private CouponItem couponItem = new CouponItem();

    private BigDecimal originalAmount = BigDecimal.ZERO;

    private BigDecimal discountAmount = BigDecimal.ZERO;

    private BigDecimal finalAmount = BigDecimal.ZERO;

    private PaymentStatus status = PaymentStatus.PENDING;

    @Override
    public Payment create() {
        Payment entity = new Payment();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public Payment withOrderAndStatus(Order mockOrder, PaymentStatus paymentStatus) {
        Payment entity = new Payment();
        this.order = mockOrder;
        this.status = paymentStatus;
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public Payment withFinalAmountAndOrder(BigDecimal finalAmount, Order order) {
        Payment entity = new Payment();
        this.order = order;
        this.finalAmount = finalAmount;
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public Payment withAmount(BigDecimal originalAmount, BigDecimal discountAmount) {
        Payment entity = new Payment();
        this.originalAmount = originalAmount;
        this.discountAmount = discountAmount;
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
}

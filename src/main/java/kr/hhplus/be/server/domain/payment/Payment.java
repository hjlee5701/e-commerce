package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.interfaces.code.PaymentErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Payment {
    @Id
    @GeneratedValue
    @Column(name = "PAYMENT_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUPON_ITEM_ID")
    private CouponItem couponItem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private BigDecimal originalAmount;

    private BigDecimal discountAmount;

    private BigDecimal finalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public static Payment create(Order order, Long requestMemberId) {
        requireNonNull(order);
        requireNonNull(order.getMember());
        requireNonNull(requestMemberId);
        validatePaymentMember(order.getMember().getId(), requestMemberId);
        BigDecimal orderTotalAmount = order.getTotalAmount();
        return new Payment(null, order, null, Member.referenceById(requestMemberId), orderTotalAmount, BigDecimal.ZERO, orderTotalAmount, PaymentStatus.PENDING);
    }

    private static void validatePaymentMember(Long orderMemberId, Long requestMemberId) {
        if (!orderMemberId.equals(requestMemberId)) {
            throw new ECommerceException(PaymentErrorCode.UNMATCHED_ORDER_MEMBER);
        }
    }


    public void applyCoupon(CouponItem couponItem) {
        if (couponItem == null) {
            return;
        }
        this.discountAmount = couponItem.apply(order.getOrderedAt());
    }

    public void calculateFinalAmount() {
        this.finalAmount = originalAmount.subtract(discountAmount).max(BigDecimal.ZERO);
    }


    public void markAsPaid() {
        if (this.status != PaymentStatus.PENDING) {
            throw new ECommerceException(PaymentErrorCode.NOT_PENDING_PAYMENT, status.name());
        }
        this.status = PaymentStatus.COMPLETED;
        order.applyPayment(finalAmount);
    }
}


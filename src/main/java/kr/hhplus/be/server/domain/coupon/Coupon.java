package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
public class Coupon {
    @Id
    @GeneratedValue
    @Column(name = "COUPON_ID")
    private Long id;

    private String title;

    private int initialQuantity;

    private int remainingQuantity;

    private BigDecimal discountAmount;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    LocalDateTime issuedAt;

    LocalDateTime expiredAt;

    public void validateTime(LocalDateTime orderedAt) {
        if (status == CouponStatus.EXPIRED || orderedAt.isAfter(expiredAt)) {
            throw new CouponExpiredException(expiredAt);
        }

        if (status == CouponStatus.INACTIVE || orderedAt.isBefore(issuedAt)) {
            throw new CouponNotYetActiveException(issuedAt);
        }


    }

    public void issue(LocalDateTime now) {
        validateStatus();
        validateWithinPeriod(now);
        validateQuantity();
        remainingQuantity--;
    }


    private void validateStatus() {
        if (status != CouponStatus.ACTIVE) {
            throw new CouponInActiveException();
        }
    }

    private void validateWithinPeriod(LocalDateTime now) {
        if (now.isAfter(expiredAt)) {
            throw new CouponExpiredException(expiredAt);
        }
    }

    private void validateQuantity() {
        if (remainingQuantity <= 0) {
            throw new CouponHasNoRemainingException();
        }
    }

}

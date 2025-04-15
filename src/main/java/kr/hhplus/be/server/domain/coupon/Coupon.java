package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.interfaces.code.CouponErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
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

    private Coupon(Long couponId) {
        this.id = couponId;
    }

    public static Coupon referenceById(Long couponId) {
        return new Coupon(couponId);
    }

    public void validateTime(LocalDateTime orderedAt) {
        if (status == CouponStatus.EXPIRED || orderedAt.isAfter(expiredAt)) {
            throw new ECommerceException(CouponErrorCode.COUPON_EXPIRED, expiredAt);
        }

        if (status == CouponStatus.INACTIVE || orderedAt.isBefore(issuedAt)) {
            throw new ECommerceException(CouponErrorCode.COUPON_NOT_YET_ACTIVE, issuedAt);
        }


    }

    public CouponItem issue(LocalDateTime now, Long memberId) {
        validateStatus();
        validateWithinPeriod(now);
        validateQuantity();
        remainingQuantity--;
        return new CouponItem(null, Member.referenceById(memberId), this, CouponItemStatus.USABLE);
    }


    private void validateStatus() {
        if (status != CouponStatus.ACTIVE) {
            throw new ECommerceException(CouponErrorCode.COUPON_INACTIVE);
        }
    }

    private void validateWithinPeriod(LocalDateTime now) {
        if (now.isAfter(expiredAt)) {
            throw new ECommerceException(CouponErrorCode.COUPON_EXPIRED, expiredAt);
        }
    }

    private void validateQuantity() {
        if (remainingQuantity <= 0) {
            throw new ECommerceException(CouponErrorCode.COUPON_NO_REMAINING);
        }
    }

}

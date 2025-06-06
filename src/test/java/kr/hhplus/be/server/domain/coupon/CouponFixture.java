package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.FixtureReflectionUtils;
import kr.hhplus.be.server.common.TestFixture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static kr.hhplus.be.server.common.FixtureTestSupport.FIXED_NOW;

@Getter
@Setter
@Accessors(chain = true)
public class CouponFixture implements TestFixture<Coupon> {

    private final Long id = 1L;
    private String title = "선착순 쿠폰";
    private int initialQuantity = 100;
    private int remainingQuantity = 50;
    private BigDecimal discountAmount = BigDecimal.TEN;
    private CouponStatus status = CouponStatus.ACTIVE;
    LocalDateTime issuedAt = FIXED_NOW.minusDays(3);
    LocalDateTime expiredAt = FIXED_NOW.plusDays(3);

    @Override
    public Coupon create() {
        Coupon entity = new Coupon();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public Coupon withStatus(CouponStatus couponStatus) {
        Coupon entity = new Coupon();
        this.status = couponStatus;
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public Coupon withExpired() {
        Coupon entity = new Coupon();
        this.status = CouponStatus.EXPIRED;
        this.issuedAt = FIXED_NOW.minusDays(10);
        this.expiredAt = FIXED_NOW.minusDays(5);
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public Coupon withExpiredAt(CouponStatus couponStatus) {
        Coupon entity = new Coupon();
        this.status = couponStatus;
        this.issuedAt = FIXED_NOW.minusDays(10);
        this.expiredAt = FIXED_NOW.minusDays(5);
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public Coupon withInActive() {
        Coupon entity = new Coupon();
        this.status = CouponStatus.INACTIVE;
        this.issuedAt = FIXED_NOW.plusDays(5);
        this.expiredAt = FIXED_NOW.plusDays(10);
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public Coupon withNoRemaining() {
        Coupon entity = new Coupon();
        this.remainingQuantity = 0;
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public Coupon withIssuable() {
        Coupon entity = new Coupon();
        this.remainingQuantity = 100;
        this.issuedAt = LocalDateTime.now().minusDays(100);
        this.expiredAt = LocalDateTime.now().plusDays(100);
        this.status = CouponStatus.ACTIVE;
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

}

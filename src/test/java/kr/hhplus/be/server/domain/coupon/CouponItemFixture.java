package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.common.FixtureReflectionUtils;
import kr.hhplus.be.server.common.TestFixture;
import kr.hhplus.be.server.domain.member.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class CouponItemFixture implements TestFixture<CouponItem> {

    private final Long id = 1L;

    private Member member = new Member();

    private Coupon coupon = new Coupon();

    private CouponItemStatus status = CouponItemStatus.USABLE;


    @Override
    public CouponItem create() {
        CouponItem entity = new CouponItem();
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public CouponItem createUsable (Coupon coupon) {
        CouponItem entity = new CouponItem();
        this.status = CouponItemStatus.USABLE;
        this.coupon = coupon;
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }

    public CouponItem createUnUsable(Coupon coupon, CouponItemStatus status) {
        CouponItem entity = new CouponItem();
        this.status = status;
        this.coupon = coupon;
        FixtureReflectionUtils.reflect(entity, this);
        return entity;
    }
}

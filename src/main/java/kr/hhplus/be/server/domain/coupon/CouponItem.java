package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.member.Member;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
public class CouponItem {
    @Id
    @GeneratedValue
    @Column(name = "COUPON_ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUPON_ID")
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    private CouponItemStatus status;

    private void validateUsable() {
        if (status != CouponItemStatus.USABLE) {
            throw new UnUsableCouponItemException();
        }
    }

    public BigDecimal apply(LocalDateTime orderedAt) {
        validateUsable();
        coupon.validateTime(orderedAt);

        status = CouponItemStatus.USED;
        return coupon.getDiscountAmount();
    }
}

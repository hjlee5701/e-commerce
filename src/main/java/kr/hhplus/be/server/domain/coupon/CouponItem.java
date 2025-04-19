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
            throw new ECommerceException(CouponErrorCode.UNUSABLE_COUPON_ITEM);
        }
    }

    public BigDecimal apply(LocalDateTime orderedAt) {
        validateUsable();
        coupon.validateTime(orderedAt);

        status = CouponItemStatus.USED;
        return coupon.getDiscountAmount();
    }

    public void checkOwner(Long memberId) {
        if (!this.member.getId().equals(memberId)) {
            throw new ECommerceException(CouponErrorCode.COUPON_ITEM_ACCESS_DENIED);
        }
    }
}

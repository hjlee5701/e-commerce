package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.member.Member;
import lombok.Getter;

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
    private CouponItemStatus couponItemStatus;
}

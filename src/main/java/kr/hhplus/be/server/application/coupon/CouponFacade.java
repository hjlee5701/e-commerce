package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.member.MemberService;
import kr.hhplus.be.server.support.lock.DistributedLock;
import kr.hhplus.be.server.support.lock.LockType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CouponFacade {

    private final MemberService memberService;
    private final CouponService couponService;

    @Transactional
    @DistributedLock(type = LockType.SPIN, keyExpression = "'COUPON:' + #criteria.couponId")
    public CouponResult.Issued issue(CouponCriteria.Issue criteria) {

        // 회원 조회
        memberService.findMemberById(criteria.toFindMemberCommand());

        // 쿠폰 발급
        CouponInfo.Issued couponInfo = couponService.issue(criteria.toIssueCouponCommand());

        return CouponResult.Issued.of(couponInfo);
    }
}

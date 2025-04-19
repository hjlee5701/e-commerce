package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.member.MemberInfo;
import kr.hhplus.be.server.domain.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponFacade {

    private final MemberService memberService;
    private final CouponService couponService;

    public CouponResult.Issued issue(CouponCriteria.Issue criteria) {

        // 회원 조회
        memberService.findMemberById(criteria.toFindMemberCommand());

        // 쿠폰 발급
        CouponInfo.Issued couponInfo = couponService.issue(criteria.toIssueCouponCommand());

        return CouponResult.Issued.of(couponInfo);
    }
}

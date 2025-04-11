package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.member.Member;
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
        Member member = memberService.findMemberById(criteria.toFindMemberCommand());

        // 쿠폰 상태/시간 확인 및 수량 차감
        Coupon coupon = couponService.issuable(criteria.toIssuableCouponCommand());

        // 쿠폰 아이템 생성
        CouponItem couponItem = couponService.issue(criteria.toIssueCouponCommand(coupon, member));

        return CouponResult.Issued.of(coupon, couponItem);
    }
}

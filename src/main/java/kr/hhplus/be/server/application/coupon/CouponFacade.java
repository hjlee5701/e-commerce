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
        MemberInfo.Detail memberInfo = memberService.findMemberById(criteria.toFindMemberCommand());

        // 쿠폰 상태/시간 확인 및 수량 차감
        CouponInfo.Issuable couponInfo = couponService.issuable(criteria.toIssuableCouponCommand());

        // 쿠폰 아이템 생성
        CouponInfo.Issued couponItemInfo = couponService.issue(criteria.toIssueCouponCommand(couponInfo, memberInfo));

        return CouponResult.Issued.of(couponInfo, couponItemInfo);
    }
}

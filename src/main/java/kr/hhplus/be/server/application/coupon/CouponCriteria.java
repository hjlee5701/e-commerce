package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponInfo;
import kr.hhplus.be.server.domain.member.MemberCommand;
import kr.hhplus.be.server.domain.member.MemberInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class CouponCriteria {

    @AllArgsConstructor
    @Getter
    public static class Issue {
        private Long couponId;
        private Long memberId;

        public static Issue of(Long couponId, Long memberId) {
            return new Issue(couponId, memberId);

        }

        public MemberCommand.Find toFindMemberCommand() {
            return new MemberCommand.Find(memberId);
        }

        public CouponCommand.Issuable toIssuableCouponCommand(){
            return new CouponCommand.Issuable(couponId);
        }
        public CouponCommand.Issue toIssueCouponCommand(CouponInfo.Issuable couponInfo, MemberInfo.Detail memberInfo) {
            return new CouponCommand.Issue(couponInfo.getCouponId(), memberInfo.getMemberId());
        }
    }
}

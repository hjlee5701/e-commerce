package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.member.MemberCommand;
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

        public CouponCommand.Issue toIssueCouponCommand(){
            return new CouponCommand.Issue(couponId, memberId);
        }
    }
}

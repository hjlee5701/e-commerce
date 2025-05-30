package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.application.coupon.CouponCriteria;
import kr.hhplus.be.server.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class CouponCommand {

    @AllArgsConstructor
    @Getter
    public static class Holdings {
        private Long memberId;
    }
    @AllArgsConstructor
    @Getter
    public static class Find {
        private Long couponItemId;
    }
    @AllArgsConstructor
    @Getter
    public static class Issue {
        private Long couponId;
        private Long memberId;

    }

    @AllArgsConstructor
    @Getter
    public static class IssueV2 {
        private Long couponId;
        private String memberId;

        public static IssueV2 of(Long couponId, String memberId) {
            return new IssueV2(couponId, memberId);
        }
    }
}

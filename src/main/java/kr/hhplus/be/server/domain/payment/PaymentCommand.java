package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.order.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PaymentCommand {

    @AllArgsConstructor
    @Getter
    public static class Pay {
        private Order order;
        private Long memberId;
        private CouponItem couponItem;
    }



}

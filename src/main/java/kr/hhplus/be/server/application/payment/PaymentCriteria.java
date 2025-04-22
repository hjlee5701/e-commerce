package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.member.MemberCommand;
import kr.hhplus.be.server.domain.memberPoint.MemberPointCommand;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentCommand;
import kr.hhplus.be.server.domain.product.ProductStockCommand;
import kr.hhplus.be.server.interfaces.payment.PaymentRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class PaymentCriteria {

    @Getter
    @AllArgsConstructor
    public static class Pay {
        private Long orderId;
        private Long couponItemId;
        private Long memberId;

        public static Pay of(Long orderId, PaymentRequest.Pay request) {
            return new Pay(orderId, request.getCouponItemId(), request.getMemberId());
        }

        public OrderCommand.Find toFindOrderCommand() {
            return new OrderCommand.Find(orderId);
        }

        public CouponCommand.Find toFindCouponItem() {
            return new CouponCommand.Find(couponItemId);
        }

        public MemberPointCommand.Use toUseMemberPointCommand(Payment payment, Order order) {
            return new MemberPointCommand.Use(payment.getFinalAmount(), order.getMember().getId());
        }

        public MemberCommand.Find toFindMemberCommand() {
            return new MemberCommand.Find(memberId);
        }

        public PaymentCommand.Pay toPayCommand(Order order, CouponItem couponItem) {
            return new PaymentCommand.Pay(order, memberId, couponItem);
        }

        public ProductStockCommand.Decrease toDecreaseStockCommand(List<OrderItem> orderItems) {
            Map<Long, Integer> productMap = orderItems.stream()
                    .collect(Collectors.toMap(orderItem ->
                            orderItem.getProduct().getId(),
                            OrderItem::getQuantity,
                            Integer::sum // 상품 ID 중복 처리: 값을 합침
                    ));
            return new ProductStockCommand.Decrease(productMap);
        }
    }
}

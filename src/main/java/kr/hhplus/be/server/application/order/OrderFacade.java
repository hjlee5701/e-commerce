package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberService;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderFacade {

    private final OrderService orderService;
    private final CouponService couponService;
    private final MemberService memberService;
    private final ProductService productService;


    public OrderResult.Created createOrder(OrderCriteria.Create criteria) {

        Member member = memberService.findMemberById(criteria.toFindMemberCommand());

        CouponItem couponItem = null;
        if (criteria.getCouponItemId() != null) {
            couponItem = couponService.getUsableCoupon(criteria.toGetUsableCouponCommand());
        }
        ProductInfo.Decreased productInfo = productService.decreaseStock(criteria.toDecreaseStockCommand());

        OrderInfo.Created info = orderService.create(criteria.toCreateOrderCommand(member, couponItem, productInfo));

        return OrderResult.Created.of(info);
    }
}

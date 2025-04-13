package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberService;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderFacade {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ProductService productService;


    public OrderResult.Created createOrder(OrderCriteria.Create criteria) {

        // 회원 조회
        Member member = memberService.findMemberById(criteria.toFindMemberCommand());

        // 재고 감소
        List<ProductInfo.Detail> productInfos = productService.decreaseStock(criteria.toDecreaseStockCommand());

        // 주문 생성
        OrderInfo.Created info = orderService.create(criteria.toCreateOrderCommand(member.getId(), productInfos));

        return OrderResult.Created.of(info);
    }
}

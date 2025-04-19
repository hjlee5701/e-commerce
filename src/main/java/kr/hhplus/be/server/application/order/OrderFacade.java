package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.member.MemberService;
import kr.hhplus.be.server.domain.order.OrderInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OrderFacade {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ProductService productService;
    private final ProductStockService productStockService;


    public OrderResult.Created createOrder(OrderCriteria.Create criteria) {

        // 회원 조회
        memberService.findMemberById(criteria.toFindMemberCommand());

        // 주문에 필요한 상품 조회
        List<ProductInfo.Detail> productInfos = productService.findProductsByIds(criteria.toFindProductsCommand());

        // 재고 감소
        productStockService.decreaseStock(criteria.toDecreaseStockCommand());

        // 주문 생성
        OrderInfo.Created info = orderService.create(criteria.toCreateOrderCommand(productInfos));

        return OrderResult.Created.of(info);
    }
}

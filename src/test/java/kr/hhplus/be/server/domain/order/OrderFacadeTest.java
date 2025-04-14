package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.domain.member.MemberInfo;
import kr.hhplus.be.server.domain.member.MemberService;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class OrderFacadeTest {

    @Mock
    private MemberService memberService;
    @Mock
    private OrderService orderService;
    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderFacade facade;

    @Test
    @DisplayName("회원 조회 → 재고 차감 → 주문 생성 순으로 서비스가 호출된다")
    void 서비스_호출_순서가_정상적으로_보장된다() {
        // given
        Long productId = 1L;

        List<OrderCriteria.ItemCreate> items = List.of(new OrderCriteria.ItemCreate(productId, 1));
        OrderCriteria.Create criteria = new OrderCriteria.Create(ANY_MEMBER_ID, items);

        // mock 결과 설정
        given(memberService.findMemberById(any())).willReturn(mock(MemberInfo.Detail.class));
        given(productService.decreaseStock(any())).willReturn(List.of(mock(ProductInfo.Detail.class)));
        given(orderService.create(any())).willReturn(mock(OrderInfo.Created.class));


        // when
        facade.createOrder(criteria);

        // then
        InOrder inOrder = inOrder(memberService, productService, orderService);
        inOrder.verify(memberService).findMemberById(any());
        inOrder.verify(productService).decreaseStock(any());
        inOrder.verify(orderService).create(any());
    }




}

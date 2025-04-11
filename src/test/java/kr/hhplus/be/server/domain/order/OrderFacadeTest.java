package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.OrderResult;
import kr.hhplus.be.server.domain.member.MemberService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductFixture;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER;
import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

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
        BigDecimal amount = BigDecimal.valueOf(1000);
        ProductFixture fixture = new ProductFixture();
        Product product1 = fixture.create();
        Product product2 = fixture.create();

        List<OrderCriteria.ItemCreate> items = List.of(
                new OrderCriteria.ItemCreate(product1.getId(), product1.getQuantity()),
                new OrderCriteria.ItemCreate(product2.getId(), product2.getQuantity())
        );
        var criteria = new OrderCriteria.Create(ANY_MEMBER_ID, items);

        ProductInfo.Decreased decreased = ProductInfo.Decreased.of(
                BigDecimal.valueOf(15000),
                List.of(
                        new ProductInfo.ItemDecreased(product1, amount, 0),
                        new ProductInfo.ItemDecreased(product2, amount, 1)
                )
        );

        OrderInfo.Created created = new OrderInfo.Created(
                99L, "PAID", BigDecimal.valueOf(15000), LocalDateTime.now(),
                List.of(
                        new OrderInfo.ItemCreated(1L, "상품 A", new BigDecimal("10000"), 1),
                        new OrderInfo.ItemCreated(2L, "상품 B", new BigDecimal("5000"), 1)
                )
        );

        given(memberService.findMemberById(any())).willReturn(ANY_MEMBER);
        given(productService.decreaseStock(any())).willReturn(decreased);
        given(orderService.create(any())).willReturn(created);

        // when
        OrderResult.Created result = facade.createOrder(criteria);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(99L);
        assertThat(result.getOrderItems()).hasSize(2);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("15000");

        InOrder inOrder = inOrder(memberService, productService, orderService);
        inOrder.verify(memberService).findMemberById(any());
        inOrder.verify(productService).decreaseStock(any());
        inOrder.verify(orderService).create(any());
    }



}

package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;


    @Test
    void 주문_생성_요청시_주문과_주문상품이_저장되고_결과를_반환한다() {
        // given
        ProductFixture productFixture = new ProductFixture();
        Product productA = productFixture.createWithStock(100);
        Product productB = productFixture.createWithStock(200);
        BigDecimal totalAmount = productA.getPrice().add(productB.getPrice());

        OrderCommand.ItemCreate itemA = new OrderCommand.ItemCreate(productA, productA.getPrice(), 2);
        OrderCommand.ItemCreate itemB = new OrderCommand.ItemCreate(productB, productB.getPrice(), 1);
        OrderCommand.Create createCommand = new OrderCommand.Create(ANY_MEMBER, List.of(itemA, itemB), totalAmount);

        Order order = new OrderFixture().create();
        List<OrderItem> orderItems = List.of(
                new OrderItem(1L, "상품 A", order, productA, productA.getPrice(), 2),
                new OrderItem(2L, "상품 B", order, productB, productB.getPrice(), 1)
        );

        given(orderRepository.save(any())).willReturn(order);
        given(orderItemRepository.saveAll(any())).willReturn(orderItems);

        // when
        OrderInfo.Created result = orderService.create(createCommand);

        // then
        verify(orderRepository, times(1)).save(any());
        verify(orderItemRepository, times(1)).saveAll(any());

        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo(order.getId());
        assertThat(result.getItems()).hasSize(2);
    }
}

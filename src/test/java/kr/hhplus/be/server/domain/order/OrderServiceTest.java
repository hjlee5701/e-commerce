package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.OrderErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;


    @Test
    @DisplayName("주문 요청한 상품의 ID 중 존재하지 않는 ID 가 있는 경우, 예외 발생한다.")
    void 주문_생성_시_존재하지_않는_상품ID로_예외() {
        // given
        Long invalidProductId = 999L;

        OrderCommand.ItemCreate itemA = new OrderCommand.ItemCreate(1L, "상의", BigDecimal.TEN, 2);
        OrderCommand.ItemCreate itemB = new OrderCommand.ItemCreate(2L, "하의", BigDecimal.TEN, 1);
        Map<Long, Integer> orderProductMap = Map.of();

        OrderCommand.Create createCommand = new OrderCommand.Create(
                ANY_MEMBER_ID, List.of(itemA, itemB), orderProductMap
        );

        // when
        assertThatThrownBy(() -> orderService.create(createCommand))
                .isInstanceOf(ECommerceException.class)
                .hasMessage(OrderErrorCode.INVALID_ORDER_PRODUCT.getMessage());
    }

    @Test
    @DisplayName("정상적인 주문 생성 요청 시 저장되고 응답이 반환된다")
    void 주문_성공_테스트() {
        // given
        OrderCommand.ItemCreate item = new OrderCommand.ItemCreate(1L, "상의", BigDecimal.TEN, 100);
        Map<Long, Integer> quantityMap = Map.of(1L, 2);

        OrderCommand.Create command = new OrderCommand.Create(ANY_MEMBER_ID, List.of(item), quantityMap);

        given(orderRepository.save(any())).willAnswer(inv -> inv.getArgument(0));
        given(orderItemRepository.saveAll(any())).willReturn(List.of(mock(OrderItem.class)));

        // when
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        OrderInfo.Created result = orderService.create(command);

        // then
        assertNotNull(result);
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        verify(orderItemRepository, times(1)).saveAll(any());

        assertThat(ANY_MEMBER_ID).isEqualTo(orderCaptor.getValue().getMember().getId());
        assertThat(1).isEqualTo(orderCaptor.getValue().getOrderItems().size());
    }
}

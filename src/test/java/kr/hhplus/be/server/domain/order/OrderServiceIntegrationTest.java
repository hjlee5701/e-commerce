package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.support.TestDataFactory;
import kr.hhplus.be.server.support.TestDataManager;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static kr.hhplus.be.server.common.FixtureTestSupport.FIXED_NOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@Transactional
public class OrderServiceIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TestDataFactory factory;

    @Autowired
    private TestDataManager testDataManager;

    private Member member;
    private Product productA;
    private Product productB;

    void setUp() {
        List<Product> products = factory.createProducts(2);
        testDataManager.persist(products);
        productA = products.get(0);
        productB = products.get(1);

        member = factory.createMember();
        testDataManager.persist(member);
    }

    @Test
    @DisplayName("통합 테스트 - 정상적인 상품 요청 시 주문이 성공적으로 생성된다")
    void 상품_주문_성공() {
        // given
        setUp();
        testDataManager.flushAndClear();

        int orderQuantityOfShirts = 1;
        int orderQuantityOfPants = 2;

        OrderCommand.Create command = getCommand(orderQuantityOfShirts, orderQuantityOfPants);
        OrderInfo.Created info = orderService.create(command);
        testDataManager.flushAndClear();

        // then
        Order order = orderRepository.findById(info.getOrderId())
                .orElse(null);

        assertThat(info).isNotNull();
        assertThat(order).isNotNull();

        List<OrderItem> orderItems = order.getOrderItems();
        assertThat(orderItems).isNotNull();

        var totalPrice = productA.getPrice().multiply(BigDecimal.valueOf(orderQuantityOfShirts))
                .add(productB.getPrice().multiply(BigDecimal.valueOf(orderQuantityOfPants)));

        // 검증
        assertAll(
                () -> assertThat(order.getId()).isEqualTo(info.getOrderId()),
                () -> assertEquals(0, totalPrice.compareTo(order.getTotalAmount())),
                () -> assertThat(orderItems.size()).isEqualTo(info.getItems().size())
        );
    }

    @NotNull
    private OrderCommand.Create getCommand(int orderQuantityOfShirts, int orderQuantityOfPants) {
        List<OrderCommand.ItemCreate> itemCommand = List.of(
                new OrderCommand.ItemCreate(productA.getId(), productA.getTitle(), productA.getPrice(), productA.getQuantity()),
                new OrderCommand.ItemCreate(productB.getId(), productB.getTitle(), productB.getPrice(), productB.getQuantity())
        );

        Map<Long, Integer> orderProductMap = Map.of(
                productA.getId(), orderQuantityOfShirts, productB.getId(), orderQuantityOfPants
        ); // 가격과 관계없이 주문 수량만 동일하게 설정

        return new OrderCommand.Create(member.getId(), itemCommand, orderProductMap);
    }


    @Test
    @DisplayName("1일 전부터 오늘까지 OrderInfo.Paid 인 주문이 반환된다")
    void 결제완료_주문_조회() {
        // given
        setUp();
        testDataManager.flushAndClear();

        // 주문1: 포함되어야 함 (1일 전)
        Order validOrder = factory.createPaidOrder(member, FIXED_NOW, productA, 1);

        // 주문2: 제외되어야 함 (5일 전)
        Order order = factory.createPaidOrder(member, FIXED_NOW, productB, 5);
        testDataManager.persist(List.of(validOrder, order));
        testDataManager.persist(validOrder.getOrderItems());
        testDataManager.persist(order.getOrderItems());

        testDataManager.flushAndClear();
        // when
        List<OrderInfo.Paid> result = orderService.getPaidOrderByDate(FIXED_NOW);

        // then
        assertThat(result).hasSize(1);

        OrderInfo.Paid paid = result.get(0);
        var soldQuantity = validOrder.getOrderItems().get(0).getQuantity();

        assertEquals(paid.getProductId(), productA.getId());
        assertEquals(soldQuantity, paid.getOrderQuantity());
    }


}

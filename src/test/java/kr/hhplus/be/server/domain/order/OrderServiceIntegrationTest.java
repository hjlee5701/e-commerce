package kr.hhplus.be.server.domain.order;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@Transactional
public class OrderServiceIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private EntityManager entityManager;

    private Member member;
    private Product shirts;
    private Product pants;

    @BeforeEach
    void setUp() {
        member = new Member(null, "tester", LocalDateTime.now());
        memberRepository.save(member);

        shirts = new Product(null, "상품A", BigDecimal.valueOf(10000), 100);
        pants = new Product(null, "상품B", BigDecimal.valueOf(20000), 100);
        productRepository.saveAll(List.of(shirts, pants));

        entityManager.flush();
    }

    @Test
    @DisplayName("통합 테스트 - 정상적인 상품 요청 시 주문이 성공적으로 생성된다")
    void 상품_주문_성공() {
        // given
        int orderQuantityOfShirts = 1;
        int orderQuantityOfPants = 2;

        OrderCommand.Create command = getCommand(orderQuantityOfShirts, orderQuantityOfPants);
        OrderInfo.Created info = orderService.create(command);
        entityManager.flush();

        // then
        Order order = orderRepository.findById(info.getOrderId())
                .orElse(null);

        assertThat(info).isNotNull();
        assertThat(order).isNotNull();

        List<OrderItem> orderItems = order.getOrderItems();
        assertThat(orderItems).isNotNull();

        var totalPrice = shirts.getPrice().multiply(BigDecimal.valueOf(orderQuantityOfShirts))
                .add(pants.getPrice().multiply(BigDecimal.valueOf(orderQuantityOfPants)));

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
                new OrderCommand.ItemCreate(shirts.getId(), shirts.getTitle(), shirts.getPrice(), shirts.getQuantity()),
                new OrderCommand.ItemCreate(pants.getId(), pants.getTitle(), pants.getPrice(), pants.getQuantity())
        );

        Map<Long, Integer> orderProductMap = Map.of(
                shirts.getId(), orderQuantityOfShirts, pants.getId(), orderQuantityOfPants
        ); // 가격과 관계없이 주문 수량만 동일하게 설정

        return new OrderCommand.Create(member.getId(), itemCommand, orderProductMap);
    }

}

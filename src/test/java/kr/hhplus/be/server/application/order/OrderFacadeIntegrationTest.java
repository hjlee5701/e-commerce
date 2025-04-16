package kr.hhplus.be.server.application.order;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderItem;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.order.OrderStatus;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.domain.product.ProductStockRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@Transactional
public class OrderFacadeIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductStockRepository productStockRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;

    private Member member;
    private Product shirts;
    private ProductStock shirtsStock;
    private Product pants;
    private ProductStock pantsStock;

    @BeforeEach
    void setUp() {
        member = new Member(null, "tester", LocalDateTime.now());
        memberRepository.save(member);

        shirts = new Product(null, "상품A", BigDecimal.valueOf(10000), 100);
        pants = new Product(null, "상품B", BigDecimal.valueOf(20000), 100);
        productRepository.saveAll(List.of(shirts, pants));

        shirtsStock = new ProductStock(null, shirts, shirts.getQuantity());
        pantsStock = new ProductStock(null, pants, pants.getQuantity());
        productStockRepository.saveAll(List.of(shirtsStock, pantsStock));

        entityManager.flush();
    }

    @Test
    @DisplayName("통합 테스트 - 정상적인 상품 요청 시 주문이 성공적으로 생성된다")
    void 상품_주문_성공() {
        Member member = new Member(null, "test", LocalDateTime.now());
        Member savedMember = memberRepository.save(member);

        int shirtsOriginalQuantity = shirtsStock.getQuantity();
        int pantsOriginalQuantity = pantsStock.getQuantity();

        int orderQuantityOfShirts = 1;
        int orderQuantityOfPants = 2;

        List<OrderCriteria.ItemCreate> itemCriteria = List.of(
                new OrderCriteria.ItemCreate(shirts.getId(), orderQuantityOfShirts),
                new OrderCriteria.ItemCreate(pants.getId(), orderQuantityOfPants)
        );

        // when
        OrderCriteria.Create criteria = new OrderCriteria.Create(savedMember.getId(), itemCriteria);
        OrderResult.Created result = orderFacade.createOrder(criteria);
        entityManager.flush();

        // then
        Order order = orderRepository.findById(result.getOrderId()).orElse(null);
        shirtsStock = productStockRepository.findByProductId(shirts.getId()).orElse(null);
        pantsStock = productStockRepository.findByProductId(pants.getId()).orElse(null);

        assertThat(result).isNotNull();
        assertThat(order).isNotNull();

        assertAll("주문 검증",
                () -> assertThat(result.getOrderId()).isNotNull(),
                () -> assertThat(order.getId()).isEqualTo(result.getOrderId()),
                () -> assertEquals(OrderStatus.PENDING, order.getStatus()),

                () -> assertEquals(shirtsOriginalQuantity-orderQuantityOfShirts, shirtsStock.getQuantity()),
                () -> assertEquals(pantsOriginalQuantity-orderQuantityOfPants, pantsStock.getQuantity())
        );

        assertThat(order.getOrderItems()).isNotEmpty();
        List<OrderItem> orderItems = order.getOrderItems();
        assertAll("주문 상품 검증",
                () -> assertThat(orderItems.size()).isEqualTo(result.getOrderItems().size()),
                () -> assertEquals(result.getOrderItems().size(), 2),
                () -> assertThat(orderItems.stream().map(orderItem -> orderItem.getProduct().getId()))
                        .containsExactlyInAnyOrder(shirts.getId(), pants.getId())
        );
    }

}

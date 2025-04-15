package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberRepository;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductRepository;
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
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("통합 테스트 - 정상적인 상품 요청 시 주문이 성공적으로 생성된다")
    void 상품_주문_성공() {
        Member member = new Member(null, "test", LocalDateTime.now());
        Member savedMember = memberRepository.save(member);

        int quantityA = 100;
        int quantityB = 200;
        int orderQuantity = 10;

        Product productA = new Product(null, "상품A", BigDecimal.valueOf(10000), quantityA);
        Product productB = new Product(null, "상품B", BigDecimal.valueOf(10000), quantityB);
        productRepository.saveAll(List.of(productA, productB));

        List<OrderCriteria.ItemCreate> itemCriteria = List.of(
                new OrderCriteria.ItemCreate(productA.getId(), orderQuantity), // 주문 수량 동일 하게 설정
                new OrderCriteria.ItemCreate(productB.getId(), orderQuantity)  // 주문 수량 동일 하게 설정
        );

        // when
        OrderCriteria.Create criteria = new OrderCriteria.Create(savedMember.getId(), itemCriteria);
        OrderResult.Created result = orderFacade.createOrder(criteria);

        // then
        Order order = orderRepository.findById(result.getOrderId())
                .orElse(null);

        assertThat(result).isNotNull();
        assertThat(order).isNotNull();

        assertAll(
                () -> assertThat(result.getOrderId()).isNotNull(),
                () -> assertThat(order.getId()).isEqualTo(result.getOrderId()),
                () -> assertThat(order.getOrderItems().size()).isEqualTo(result.getOrderItems().size()),
                () -> assertEquals(quantityA-orderQuantity, productA.getQuantity()),
                () -> assertEquals(quantityB-orderQuantity, productB.getQuantity())
        );
    }

}

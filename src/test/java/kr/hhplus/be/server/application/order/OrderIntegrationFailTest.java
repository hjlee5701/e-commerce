package kr.hhplus.be.server.application.order;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Testcontainers
@Transactional
public class OrderIntegrationFailTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductStockRepository productStockRepository;
    @Autowired
    private EntityManager entityManager;
    private Member member;
    private Product product;
    private ProductStock productStock;

    @BeforeEach
    void setUp() {
        member = new Member(null, "tester", LocalDateTime.now());
        memberRepository.save(member);

        product = new Product(null, "상품A", BigDecimal.valueOf(10000), 100);
        productRepository.saveAll(List.of(product));

        productStock = new ProductStock(null, product, product.getQuantity());
        productStockRepository.saveAll(List.of(productStock));
        entityManager.flush();
    }


    @DisplayName("동시에 주문 요청 시 실패")
    @Test
    void verifyDeadlockDuringConcurrentOrderPayment() throws InterruptedException {
        // given
        int orderQuantity = 1;

        List<OrderCriteria.ItemCreate> itemCriteria = List.of(
                new OrderCriteria.ItemCreate(product.getId(), orderQuantity)
        );

        // when
        OrderCriteria.Create criteria = new OrderCriteria.Create(member.getId(), itemCriteria);

        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Future<OrderResult.Created>> futures = new ArrayList<>();
        // when
        for (int i = 0; i < threadCount; i++) {
            Future<OrderResult.Created> future = executorService.submit(() -> {
                try {
                    return orderFacade.createOrder(criteria);
                } finally {
                    latch.countDown();
                }
            });
            futures.add(future);
        }

        latch.await();
        executorService.shutdown();

        // then
        List<Throwable> errors = new ArrayList<>();
        for (Future<OrderResult.Created> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                errors.add(e.getCause());
            }
        }
        int expectedQuantity = productStock.getQuantity()-threadCount;
        ProductStock remainStock = productStockRepository.findByProductId(product.getId()).orElseThrow();
        assertThat(remainStock.getQuantity()).isEqualTo(expectedQuantity);
    }
}

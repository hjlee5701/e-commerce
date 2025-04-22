package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.memberPoint.MemberPoint;
import kr.hhplus.be.server.domain.memberPoint.MemberPointPolicy;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductStock;
import kr.hhplus.be.server.infrastructure.product.ProductStockJpaRepository;
import kr.hhplus.be.server.support.TestDataFactory;
import kr.hhplus.be.server.support.TestDataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
public class PaymentFacadeConcurrencyTest {

    @Autowired
    private ProductStockJpaRepository productStockRepository;

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private TestDataFactory factory;

    @Autowired
    private TestDataManager testDataManager;

    private List<Member> members;
    private List<Order> orders;

    private Product product;

    private ProductStock productStock;

    private LocalDateTime now;


    void setUp(int memberCount, int remainingStock, int orderQuantity) {
        members = new ArrayList<>();
        orders = new ArrayList<>();

        product = factory.createProductByPrice(BigDecimal.valueOf(100));
        productStock = factory.createProductStock(product, remainingStock);

        testDataManager.persist(List.of(product, productStock));

        now = LocalDateTime.now();
        for (int i = 0; i < memberCount; i++) {
            Member member = factory.createMember();
            MemberPoint memberPoint = factory.createMemberPointByBalance(member, MemberPointPolicy.MAX_BALANCE_AMOUNT);
            testDataManager.persist(List.of(member, memberPoint));
            members.add(member);

            Order order = factory.createPendingOrderByQuantity(member, List.of(product), orderQuantity, now);
            testDataManager.persist(order);
            testDataManager.persist(order.getOrderItems());
            orders.add(order);
        }
    }


    @AfterEach
    void cleanupAll() {
        testDataManager.cleanupAll();
    }


    @DisplayName("동시에 3명의 사용자가 재고가 1개인 상품을 구매하기 위해 시도할 경우, 1명만 구매에 성공한다.")
    @Test
    void 재고_차감_동시성_문제() {
        // given
        int remainingStock = 1;
        int orderQuantity = 1;
        int memberCount = 3;

        setUp(memberCount, remainingStock, orderQuantity);
        testDataManager.flushAndClear();

        int threadCount = 3;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicInteger failedMemberIds = new AtomicInteger();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < members.size(); i++) {
            int idx = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        countDownLatch.await();
                        PaymentCriteria.Pay criteria = new PaymentCriteria.Pay(orders.get(idx).getId(), null, members.get(idx).getId());
                        paymentFacade.createPayment(criteria);
                    } catch (Exception e) {
                        failedMemberIds.getAndIncrement();
                    }}, executor);
            futures.add(future);
        }
        countDownLatch.countDown();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // when
        ProductStock remainStock = productStockRepository.findByProductId(product.getId()).orElseThrow();
        assertEquals(0, remainStock.getQuantity());
        assertEquals(2, failedMemberIds.get());

    }
}

package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.coupon.CouponItemRepository;
import kr.hhplus.be.server.domain.coupon.CouponItemStatus;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.memberPoint.MemberPoint;
import kr.hhplus.be.server.domain.memberPoint.MemberPointPolicy;
import kr.hhplus.be.server.domain.memberPoint.MemberPointRepository;
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
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
public class PaymentFacadeConcurrencyTest {

    @Autowired
    private ProductStockJpaRepository productStockRepository;

    @Autowired
    private CouponItemRepository couponItemRepository;

    @Autowired
    private MemberPointRepository memberPointRepository;

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private TestDataFactory factory;

    @Autowired
    private TestDataManager testDataManager;

    private List<Member> members = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();

    private Product product;

    private ProductStock productStock;

    private LocalDateTime now;


    void setUp(int memberCount, int remainingStock, int orderQuantity) {
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
        members = new ArrayList<>();
        orders = new ArrayList<>();
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


    @Test
    @DisplayName("동시에 하나의 쿠폰을 동일한 결제에 적용하기 위해 3번 시도할 경우, 1번만 성공한다.")
    void 쿠폰_사용_동시성_테스트() {
        int remainingStock = 1;
        int orderQuantity = 1;
        int requestCount = 1;
        setUp(requestCount, remainingStock, orderQuantity);

        Member member = members.get(0);
        Order order = orders.get(0);

        Coupon coupon = factory.createCoupon(10, now);
        CouponItem couponItem = factory.createCouponItem(coupon, members.get(0));

        testDataManager.persist(List.of(coupon, couponItem));
        testDataManager.flushAndClear();

        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(0);
        AtomicInteger failedCount = new AtomicInteger();
        AtomicInteger successCount = new AtomicInteger();

        // when
        List<CompletableFuture<Void>> futures = IntStream.range(0, threadCount)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    try {
                        countDownLatch.await();
                        PaymentCriteria.Pay criteria = new PaymentCriteria.Pay(order.getId(), couponItem.getId(), member.getId());
                        paymentFacade.createPayment(criteria);

                    } catch (Exception e) {
                        failedCount.getAndIncrement();
                    }}, executorService))
                .toList();

        countDownLatch.countDown();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // then
        CouponItem usedCoupon = couponItemRepository.findById(couponItem.getId()).orElse(null);

        assertEquals(2, failedCount.get());
        assertEquals(CouponItemStatus.USED, usedCoupon.getStatus());
    }

    @Test
    @DisplayName("동시에 한 회원의 포인트를 서로 다른 결제에 적용하기 위해 3번 시도할 경우, 잔액부족으로 1번만 성공한다.")
    void 금액_결제_동시성_테스트() {

        int remainingStock = 100;
        int orderQuantity = 1;
        BigDecimal balance = BigDecimal.TEN;
        int threadCount = 3;

        product = factory.createProductByPrice(balance);
        productStock = factory.createProductStock(product, remainingStock);
        testDataManager.persist(List.of(product, productStock));

        now = LocalDateTime.now();
        Member member = factory.createMember();
        MemberPoint memberPoint = factory.createMemberPointByBalance(member, balance);
        testDataManager.persist(List.of(member, memberPoint));

        for (int i = 0; i < threadCount; i++) {
            Order order = factory.createPendingOrderByQuantity(member, List.of(product), orderQuantity, now);
            System.out.println(order.getTotalAmount());
            testDataManager.persist(order);
            testDataManager.persist(order.getOrderItems());
            orders.add(order);
        }
        testDataManager.flushAndClear();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(0);
        AtomicInteger failedCount = new AtomicInteger();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // when
        for (Order order : orders) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    countDownLatch.await();
                    PaymentCriteria.Pay criteria = new PaymentCriteria.Pay(order.getId(), null, member.getId());
                    paymentFacade.createPayment(criteria);
                } catch (Exception e) {
                    failedCount.getAndIncrement();
                }}, executor);
            futures.add(future);
        }
        countDownLatch.countDown();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // then
        MemberPoint decreasedPoint = memberPointRepository.findByMemberId(member.getId()).orElse(null);
        assertEquals(2, failedCount.get());
        assertEquals(0, BigDecimal.ZERO.compareTo(decreasedPoint.getBalance()));
    }
}

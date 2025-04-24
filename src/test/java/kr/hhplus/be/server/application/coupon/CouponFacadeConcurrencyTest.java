package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.infrastructure.coupon.CouponItemJpaRepository;
import kr.hhplus.be.server.support.TestDataFactory;
import kr.hhplus.be.server.support.TestDataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CouponFacadeConcurrencyTest {

    @Autowired
    private CouponFacade facade;

    @Autowired
    private TestDataFactory testFactory;

    @Autowired
    private TestDataManager testDataManager;

    @Autowired
    private CouponItemJpaRepository couponItemRepository;

    private Coupon coupon;

    private final List<Member> members = new ArrayList<>();


    void setUp(int requestCount, int remainingQuantity) {

        for (int i = 0; i < requestCount; i++) {
            members.add(testFactory.createMember());
        }
        coupon = testFactory.createCoupon(remainingQuantity, LocalDateTime.now());

        testDataManager.persist(members);
        testDataManager.persist(coupon);
    }

    @AfterEach
    void cleanupAll() {
        testDataManager.cleanupAll();
    }


    @Test
    @DisplayName("동시에 3명의 사용자가 2개 남은 선착순 쿠폰을 발급하기 위해 시도한다.")
    void 동시에_선착순_쿠폰_발급() throws InterruptedException {
        // given
        int remainingQuantity = 2;
        int requestCount = 3;

        setUp(requestCount, remainingQuantity);
        testDataManager.flushAndClear();

        // when
        int threadCount = 3; // 실행할 스레드 수
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger failedUserIds = new AtomicInteger();

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Member member : members) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();
                    CouponCriteria.Issue criteria = CouponCriteria.Issue.of(coupon.getId(), member.getId());
                    facade.issue(criteria);
                } catch (Exception e) {
                    failedUserIds.getAndIncrement();
                }
            }, executor);

            futures.add(future);
        }

        readyLatch.await();
        startLatch.countDown();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // then
        List<CouponItem> issued = couponItemRepository.findAll();
        assertThat(issued).hasSize(2);

        Set<Long> issuedMemberIds = issued.stream()
                .map(couponItem -> couponItem.getMember().getId())
                .collect(Collectors.toSet());

        assertThat(issuedMemberIds).isSubsetOf(
                members.stream().map(Member::getId).collect(Collectors.toSet())
        );

        assertThat(failedUserIds.get()).isEqualTo(1);
    }
}

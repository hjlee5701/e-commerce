package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.infrastructure.coupon.CouponItemJpaRepository;
import kr.hhplus.be.server.support.TestDataFactory;
import kr.hhplus.be.server.support.TestDataManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

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

    private List<Member> members;


    void setUp(int remainingQuantity) {
        Member memberA = testFactory.createMember();
        Member memberB = testFactory.createMember();
        members = List.of(memberA, memberB);
        coupon = testFactory.createCoupon(remainingQuantity, LocalDateTime.now());

        testDataManager.persist(List.of(memberA, memberB, coupon));
    }


    @Test
    @DisplayName("동시에 2명의 사용자가 1개 남은 선착순 쿠폰을 발급하기 위해 시도한다.")
    void 동시에_선착순_쿠폰_발급() {
        // given
        int remainingQuantity = 1;

        setUp(remainingQuantity);
        testDataManager.flushAndClear();

        // when
        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger failedUserIds = new AtomicInteger();

        List<CompletableFuture<Void>> futures = IntStream.range(0, threadCount)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    try {
                        startLatch.await();
                        Member member = members.get(i);
                        CouponCriteria.Issue criteria = CouponCriteria.Issue.of(coupon.getId(), member.getId());
                        facade.issue(criteria);
                    } catch (Exception e) {
                        failedUserIds.getAndIncrement();
                    }
                }, executor))
                .toList();

        startLatch.countDown();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // then
        List<CouponItem> issued = couponItemRepository.findAll();
        assertThat(issued).hasSize(1);

        Long issuedMemberId = issued.get(0).getMember().getId();
        assertThat(members).extracting(Member::getId).contains(issuedMemberId);

        assertThat(failedUserIds.get()).isEqualTo(1);
    }
}

package kr.hhplus.be.server.concurrency.memberPoint;

import kr.hhplus.be.server.application.memberPoint.MemberPointCriteria;
import kr.hhplus.be.server.application.memberPoint.MemberPointFacade;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.memberPoint.MemberPoint;
import kr.hhplus.be.server.domain.memberPoint.MemberPointRepository;
import kr.hhplus.be.server.support.TestDataFactory;
import kr.hhplus.be.server.support.TestDataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
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
public class MemberPointFacadeConcurrencyTest {


    @Autowired
    private MemberPointRepository memberPointRepository;

    @Autowired
    private MemberPointFacade memberPointFacade;

    @Autowired
    private TestDataFactory factory;

    @Autowired
    private TestDataManager testDataManager;

    private Member member;


    void setUp(BigDecimal balance) {
        member = factory.createMember();
        MemberPoint memberPoint = factory.createMemberPointByBalance(member, balance);
        testDataManager.persist(List.of(member, memberPoint));
    }


    @AfterEach
    void cleanupAll() {
        testDataManager.cleanupAll();
    }

    @Test
    @DisplayName("동시에 한 회원의 포인트 충전을 위해 20번을 시도할 경우, 재시도로 일부만 성공한다.")
    void 충전_동시성_문제() throws InterruptedException {
        // given
        BigDecimal balance = BigDecimal.ZERO;
        setUp(balance);
        testDataManager.flushAndClear();

        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger failedCount = new AtomicInteger();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            BigDecimal amount = BigDecimal.TEN;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    readyLatch.countDown();     // 준비 완료
                    startLatch.await();         // 시작 신호 기다림
                    var criteria = new MemberPointCriteria.Charge(member.getId(), amount);
                    memberPointFacade.charge(criteria);
                } catch (Exception e) {
                    failedCount.getAndIncrement();
                }}, executor);
            futures.add(future);
        }
        readyLatch.await();
        startLatch.countDown();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // then
        MemberPoint chargedPoint = memberPointRepository.findByMemberId(member.getId()).orElse(null);

        int successCount = threadCount-failedCount.get();
        BigDecimal chargedBalance = chargedPoint.getBalance();
        assertEquals(0, chargedBalance.compareTo(BigDecimal.TEN.multiply(BigDecimal.valueOf(successCount))));
    }
}

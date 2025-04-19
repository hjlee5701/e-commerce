package kr.hhplus.be.server.application.memberPoint;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberRepository;
import kr.hhplus.be.server.domain.memberPoint.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@Transactional
public class MemberPointFacadeIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberPointRepository memberPointRepository;

    @Autowired
    private MemberPointHistoryRepository memberPointHistoryRepository;

    @Autowired
    private MemberPointFacade facade;

    @Autowired
    private EntityManager entityManager;

    private Member member;
    private MemberPoint memberPoint;

    @BeforeEach
    void setUp() {
        member = new Member(null, "tester", LocalDateTime.now());
        memberRepository.save(member);

        memberPoint = new MemberPoint(null, Member.referenceById(member.getId()), BigDecimal.TEN);
        memberPointRepository.save(memberPoint);

        entityManager.flush();
    }

    @Test
    @DisplayName("통합 테스트 - 회원 충전 성공한 경우 충전 후 잔액과 충전 이력이 저장된다.")
    void 회원_충전_성공() {

        // given
        BigDecimal chargeAmount = BigDecimal.valueOf(100);
        BigDecimal amount = memberPoint.getBalance();

        // when
        MemberPointCriteria.Charge criteria = new MemberPointCriteria.Charge(member.getId(), chargeAmount);
        MemberPointResult.ChargeBalance result = facade.charge(criteria);
        entityManager.flush();

        // then
        MemberPoint savedMemberPoint = memberPointRepository.findByMemberId(member.getId())
                .orElse(null);

        MemberPointHistory chargeHistory = memberPointHistoryRepository.findByMemberId(member.getId())
                .orElse(null);


        assertThat(result).isNotNull();
        assertThat(savedMemberPoint).isNotNull();
        assertThat(chargeHistory).isNotNull();
        assertAll("충전 금액 검증",
                () -> assertEquals(0, amount.add(chargeAmount).compareTo(result.getBalance())),
                () -> assertEquals(0, result.getBalance().compareTo(savedMemberPoint.getBalance()))
        );
        assertAll("충전 이력 검증",
                () -> assertEquals(TransactionType.CHARGE, chargeHistory.getType()),
                () -> assertEquals(0, chargeAmount.compareTo(chargeHistory.getAmount()))
        );
    }

}

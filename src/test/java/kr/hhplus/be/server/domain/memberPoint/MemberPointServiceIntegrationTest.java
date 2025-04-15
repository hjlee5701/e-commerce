package kr.hhplus.be.server.domain.memberPoint;

import jakarta.persistence.EntityManager;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberCommand;
import kr.hhplus.be.server.domain.member.MemberRepository;
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
public class MemberPointServiceIntegrationTest {


    @Autowired
    private MemberPointService memberPointService;

    @Autowired
    private MemberPointRepository memberPointRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EntityManager entityManager;

    private Member member;

    @BeforeEach
    void setUp() {
        member = new Member(null, "tester", LocalDateTime.now());
        memberRepository.save(member);

        entityManager.flush();
    }

    @Test
    @DisplayName("통합 테스트 - 회원 첫 충전 성공할 경우 0원에 충전 금액을 더한 값을 반환한다.")
    void 첫_충전_성공() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);

        MemberPointCommand.Charge command = new MemberPointCommand.Charge(
                member.getId(), amount
        );

        // when
        MemberPointInfo.Balance info = memberPointService.charge(command);
        entityManager.flush();

        // then
        MemberPoint savedMemberPoint = memberPointRepository.findByMemberId(member.getId())
                .orElse(null);

        assertThat(info).isNotNull();
        assertThat(savedMemberPoint).isNotNull();
        assertAll(
                () -> assertEquals(BigDecimal.ZERO.add(amount), info.getBalance()),
                () -> assertEquals(0, BigDecimal.ZERO.add(amount).compareTo(info.getBalance())),
                () -> assertEquals(0, info.getBalance().compareTo(savedMemberPoint.getBalance()))
        );
    }

    @Test
    @DisplayName("통합 테스트 - 기존 회원 충전 경우 잔액에 충전 금액을 더한 값을 반환한다.")
    void 기존_회원_충전_성공_테스트() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);
        MemberPoint memberPoint = new MemberPoint(null, Member.referenceById(member.getId()), amount);
        memberPointRepository.save(memberPoint);
        entityManager.flush();

        BigDecimal chargeAmount = BigDecimal.valueOf(200);
        MemberPointCommand.Charge command = new MemberPointCommand.Charge(
                member.getId(), chargeAmount
        );

        // when
        MemberPointInfo.Balance info = memberPointService.charge(command);
        entityManager.flush();

        // then
        MemberPoint savedMemberPoint = memberPointRepository.findByMemberId(member.getId())
                .orElse(null);

        assertThat(info).isNotNull();
        assertThat(savedMemberPoint).isNotNull();
        assertAll(
                () -> assertEquals(0, amount.add(chargeAmount).compareTo(info.getBalance())),
                () -> assertEquals(0, info.getBalance().compareTo(savedMemberPoint.getBalance()))
        );
    }

    @Test
    @DisplayName("통합 테스트 - 회원의 잔액 조회 성공할 경우 현재 남은 잔액을 반환한다.")
    void 잔액_조회_성공() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);

        MemberPoint memberPoint = new MemberPoint(null, Member.referenceById(member.getId()), amount);
        memberPointRepository.save(memberPoint);
        entityManager.flush();

        // when
        MemberCommand.Find command = new MemberCommand.Find(member.getId());
        MemberPointInfo.Balance info = memberPointService.getBalance(command);
        entityManager.flush();

        // then
        MemberPoint savedMemberPoint = memberPointRepository.findByMemberId(member.getId())
                .orElse(null);;

        assertThat(info).isNotNull();
        assertThat(savedMemberPoint).isNotNull();
        assertEquals(0, info.getBalance().compareTo(savedMemberPoint.getBalance()));
    }

    @Test
    @DisplayName("통합 테스트 - 회원의 잔액을 사용 성공할 경우, 잔액은 차감된다.")
    void 잔액_차감_성공() {
        // given
        BigDecimal useAmount = BigDecimal.valueOf(100);
        BigDecimal balance = BigDecimal.valueOf(300);

        MemberPoint memberPoint = new MemberPoint(null, Member.referenceById(member.getId()), balance);
        memberPointRepository.save(memberPoint);
        entityManager.flush();

        // when
        MemberPointCommand.Use command = new MemberPointCommand.Use(
                useAmount, member.getId()
        );
        memberPointService.use(command);
        entityManager.flush();


        // then
        MemberPoint savedMemberPoint = memberPointRepository.findByMemberId(member.getId())
                .orElse(null);;
        assertThat(savedMemberPoint).isNotNull();
        assertEquals(0, balance.subtract(useAmount).compareTo(savedMemberPoint.getBalance()));
    }

}

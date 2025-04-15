package kr.hhplus.be.server.domain.memberPoint;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Test
    @DisplayName("통합 테스트 - 회원 첫 충전 성공할 경우 0원에 충전 금액을 더한 값을 반환한다.")
    void 첫_충전_성공() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);
        Member member = new Member(null, "test", LocalDateTime.now());
        Member savedMember = memberRepository.save(member);

        MemberPointCommand.Charge command = new MemberPointCommand.Charge(
                savedMember.getId(), amount
        );

        // when
        MemberPointInfo.Balance info = memberPointService.charge(command);

        // then
        assertAll(
                () -> assertEquals(BigDecimal.ZERO.add(amount), info.getBalance())
        );
    }

    @Test
    @DisplayName("통합 테스트 - 기존 회원 충전 경우 잔액에 충전 금액을 더한 값을 반환한다.")
    void 기존_회원_충전_성공_테스트() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal chargeAmount = BigDecimal.valueOf(200);
        Member member = new Member(null, "test", LocalDateTime.now());
        Member savedMember = memberRepository.save(member);


        MemberPoint memberPoint = new MemberPoint(null, Member.referenceById(savedMember.getId()), amount);
        memberPointRepository.save(memberPoint);

        MemberPointCommand.Charge command = new MemberPointCommand.Charge(
                savedMember.getId(), chargeAmount
        );

        // when
        MemberPointInfo.Balance info = memberPointService.charge(command);

        // then
        assertEquals(amount.add(chargeAmount), info.getBalance());
    }

    @Test
    @DisplayName("통합 테스트 - 회원의 잔액 조회 성공할 경우 현재 남은 잔액을 반환한다.")
    void 잔액_조회_성공() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal chargeAmount = BigDecimal.valueOf(200);
        Member member = new Member(null, "test", LocalDateTime.now());
        Member savedMember = memberRepository.save(member);


        MemberPoint memberPoint = new MemberPoint(null, Member.referenceById(savedMember.getId()), amount);
        memberPointRepository.save(memberPoint);

        MemberPointCommand.Charge command = new MemberPointCommand.Charge(
                savedMember.getId(), chargeAmount
        );

        // when
        MemberPointInfo.Balance info = memberPointService.charge(command);

        // then
        assertEquals(amount.add(chargeAmount), info.getBalance());
    }

    @Test
    @DisplayName("통합 테스트 - 회원의 잔액을 사용 성공할 경우, 잔액은 차감된다.")
    void 잔액_차감_성공() {
        // given
        BigDecimal useAmount = BigDecimal.valueOf(100);
        BigDecimal balance = BigDecimal.valueOf(300);

        Member member = new Member(null, "test", LocalDateTime.now());
        Member savedMember = memberRepository.save(member);

        MemberPoint memberPoint = new MemberPoint(null, Member.referenceById(savedMember.getId()), balance);
        memberPointRepository.save(memberPoint);

        MemberPointCommand.Use command = new MemberPointCommand.Use(
                useAmount, savedMember.getId()
                );

        // when
        memberPointService.use(command);

        // then
        assertEquals(balance.subtract(useAmount), memberPoint.getBalance());
    }

}

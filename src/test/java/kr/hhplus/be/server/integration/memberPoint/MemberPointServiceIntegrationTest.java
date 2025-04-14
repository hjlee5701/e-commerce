package kr.hhplus.be.server.integration.memberPoint;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberRepository;
import kr.hhplus.be.server.domain.memberPoint.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER_ID;
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
    void 첫_충전_성공_테스트() {
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
    void 기존_회원_충전_성공_테스트() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal chargeAmount = BigDecimal.valueOf(200);

        MemberPoint memberPoint = new MemberPoint(null, Member.referenceById(ANY_MEMBER_ID), amount);
        memberPointRepository.save(memberPoint);

        MemberPointCommand.Charge command = new MemberPointCommand.Charge(
                ANY_MEMBER_ID, amount
        );

        // when
        MemberPointInfo.Balance info = memberPointService.charge(command);

        // then
        assertEquals(amount.add(chargeAmount), info.getBalance());
    }
}

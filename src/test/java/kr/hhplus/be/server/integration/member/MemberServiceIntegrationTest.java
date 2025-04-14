package kr.hhplus.be.server.integration.member;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberCommand;
import kr.hhplus.be.server.domain.member.MemberInfo;
import kr.hhplus.be.server.domain.member.MemberService;
import kr.hhplus.be.server.domain.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@Transactional
public class MemberServiceIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 조회 성공 통합 테스트")
    void 회원_조회_통합_테스트() {
        // given
        LocalDateTime regAt = LocalDateTime.now();
        Member member = new Member(null, "tester", regAt);
        memberRepository.save(member);

        // when
        MemberCommand.Find command = new MemberCommand.Find(member.getId());
        MemberInfo.Detail info = memberService.findMemberById(command);

        // then
        assertAll(
                () -> assertEquals(member.getId(), info.getMemberId()),
                () -> assertEquals(member.getUserId(), info.getUserId()),
                () -> assertEquals(member.getRegAt(), info.getRegAt())
        );
    }

}

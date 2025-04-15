package kr.hhplus.be.server.domain.member;

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
    @DisplayName("통합 테스트 - 회원 조회 성공할 경우 상세 정보 반환한다.")
    void 회원ID로_회원정보_정상_조회() {
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

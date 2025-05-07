package kr.hhplus.be.server.domain.member;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Autowired
    private EntityManager entityManager;

    private Member member;

    void setUp() {
        member = new Member(null, "tester", LocalDateTime.now());
        memberRepository.save(member);

        cleanUp();
    }

    void cleanUp() {
        entityManager.flush();
        entityManager.clear();
    }
    
    

    @Test
    @DisplayName("통합 테스트 - 회원 조회 성공할 경우 상세 정보 반환한다.")
    void 회원ID로_회원정보_정상_조회() {
        // given
        setUp();
        MemberCommand.Find command = new MemberCommand.Find(member.getId());

        // when
        MemberInfo.Detail info = memberService.findMemberById(command);

        cleanUp();

        Member savedMember = memberRepository.findById(member.getId())
                .orElse(null);

        // then
        assertThat(savedMember).isNotNull();
        assertAll(
                () -> assertEquals(savedMember.getId(), info.getMemberId()),
                () -> assertEquals(savedMember.getUserId(), info.getUserId()),
                () -> assertEquals(savedMember.getRegAt(), info.getRegAt())
        );
    }

}

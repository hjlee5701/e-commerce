package kr.hhplus.be.server.domain.member;

import kr.hhplus.be.server.domain.member.exception.MemberNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER_ID;
import static kr.hhplus.be.server.interfaces.code.MemberErrorCode.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    void 회원_조회_실패로_예외_발생() {
        // given
        given(memberRepository.findById(ANY_MEMBER_ID)).willReturn(Optional.empty());
        var command = new MemberCommand.Find(ANY_MEMBER_ID);

        // when & then
        assertThatThrownBy(() -> memberService.findMemberById(command))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage(MEMBER_NOT_FOUND.getMessage());
    }

}

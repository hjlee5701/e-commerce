package kr.hhplus.be.server.domain.member;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public Member findMemberById(MemberCommand.Find command) {
        return memberRepository.findById(command.getMemberId())
                .orElseThrow(() -> new ECommerceException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}

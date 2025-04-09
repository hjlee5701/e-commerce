package kr.hhplus.be.server.domain.member;

import kr.hhplus.be.server.domain.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public Member findMemberById(MemberCommand.Find command) {
        return memberRepository.findById(command.getMemberId())
                .orElseThrow(MemberNotFoundException::new);
    }
}

package kr.hhplus.be.server.infrastructure.member;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MemberRepositoryImpl implements MemberRepository {
    @Override
    public Optional<Member> findById(Long memberId) {
        return Optional.empty();
    }
}

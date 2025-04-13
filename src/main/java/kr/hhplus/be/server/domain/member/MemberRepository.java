package kr.hhplus.be.server.domain.member;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository {
    Optional<Member> findById(Long memberId);
}

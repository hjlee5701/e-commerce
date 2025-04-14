package kr.hhplus.be.server.infrastructure.member;

import kr.hhplus.be.server.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberJpaRepository extends JpaRepository<Member, Long> {
}

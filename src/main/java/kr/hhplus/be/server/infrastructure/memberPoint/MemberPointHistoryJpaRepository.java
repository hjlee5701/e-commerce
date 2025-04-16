package kr.hhplus.be.server.infrastructure.memberPoint;

import kr.hhplus.be.server.domain.memberPoint.MemberPointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberPointHistoryJpaRepository extends JpaRepository<MemberPointHistory, Long> {
    Optional<MemberPointHistory> findByMemberId(Long memberId);
}

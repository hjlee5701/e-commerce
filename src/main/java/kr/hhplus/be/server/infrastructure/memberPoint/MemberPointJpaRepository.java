package kr.hhplus.be.server.infrastructure.memberPoint;

import kr.hhplus.be.server.domain.memberPoint.MemberPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberPointJpaRepository extends JpaRepository<MemberPoint, Long> {
    Optional<MemberPoint> findByMemberId(Long memberId);
}

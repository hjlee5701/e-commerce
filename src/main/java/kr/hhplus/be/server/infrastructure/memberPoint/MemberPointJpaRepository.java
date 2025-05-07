package kr.hhplus.be.server.infrastructure.memberPoint;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.memberPoint.MemberPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberPointJpaRepository extends JpaRepository<MemberPoint, Long> {
    Optional<MemberPoint> findByMemberId(Long memberId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select mp from MemberPoint mp where mp.member.id = :memberId")
    Optional<MemberPoint> findByMemberIdForUpdate(Long memberId);
}

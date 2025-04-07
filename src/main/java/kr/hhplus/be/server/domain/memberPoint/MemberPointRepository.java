package kr.hhplus.be.server.domain.memberPoint;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberPointRepository {

    Optional<MemberPoint> findByMemberId(Long id);

    Optional<MemberPoint> findById(Long memberId);

    MemberPoint save(MemberPoint memberPoint);
}

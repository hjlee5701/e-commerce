package kr.hhplus.be.server.domain.memberPoint;

import java.util.Optional;

public interface MemberPointRepository {

    Optional<MemberPoint> findByMemberId(Long id);

    Optional<MemberPoint> findById(Long memberId);

    MemberPoint save(MemberPoint memberPoint);
}

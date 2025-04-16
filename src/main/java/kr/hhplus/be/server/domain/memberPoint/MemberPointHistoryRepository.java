package kr.hhplus.be.server.domain.memberPoint;

import java.util.Optional;

public interface MemberPointHistoryRepository {
    MemberPointHistory save(MemberPointHistory history);

    Optional<MemberPointHistory> findByMemberId(Long memberId);
}

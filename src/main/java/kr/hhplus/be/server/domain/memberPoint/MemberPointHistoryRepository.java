package kr.hhplus.be.server.domain.memberPoint;

import org.springframework.stereotype.Repository;

@Repository
public interface MemberPointHistoryRepository {
    MemberPointHistory save(MemberPointHistory history);
}

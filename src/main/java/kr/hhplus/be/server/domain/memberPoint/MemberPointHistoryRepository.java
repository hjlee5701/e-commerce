package kr.hhplus.be.server.domain.memberPoint;

import kr.hhplus.be.server.domain.memberPoint.MemberPointHistory;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberPointHistoryRepository {
    MemberPointHistory save(MemberPointHistory history);
}

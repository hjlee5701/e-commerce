package kr.hhplus.be.server.infrastructure.memberPoint;

import kr.hhplus.be.server.domain.memberPoint.MemberPointHistory;
import kr.hhplus.be.server.domain.memberPoint.MemberPointHistoryRepository;
import org.springframework.stereotype.Component;

@Component
public class MemberPointHistoryRepositoryImpl implements MemberPointHistoryRepository {
    @Override
    public MemberPointHistory save(MemberPointHistory history) {
        return null;
    }
}

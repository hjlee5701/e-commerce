package kr.hhplus.be.server.infrastructure.memberPoint;

import kr.hhplus.be.server.domain.memberPoint.MemberPoint;
import kr.hhplus.be.server.domain.memberPoint.MemberPointRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MemberPointRepositoryImpl implements MemberPointRepository {
    @Override
    public Optional<MemberPoint> findByMemberId(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<MemberPoint> findById(Long memberId) {
        return Optional.empty();
    }

    @Override
    public MemberPoint save(MemberPoint memberPoint) {
        return null;
    }
}

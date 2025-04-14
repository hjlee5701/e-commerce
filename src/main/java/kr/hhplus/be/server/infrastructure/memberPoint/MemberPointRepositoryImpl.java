package kr.hhplus.be.server.infrastructure.memberPoint;

import kr.hhplus.be.server.domain.memberPoint.MemberPoint;
import kr.hhplus.be.server.domain.memberPoint.MemberPointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberPointRepositoryImpl implements MemberPointRepository {

    private final MemberPointJpaRepository memberPointJpaRepository;

    @Override
    public Optional<MemberPoint> findByMemberId(Long id) {
        return memberPointJpaRepository.findByMemberId(id);
    }

    @Override
    public Optional<MemberPoint> findById(Long memberId) {
        return memberPointJpaRepository.findById(memberId);
    }

    @Override
    public MemberPoint save(MemberPoint memberPoint) {
        return memberPointJpaRepository.save(memberPoint);
    }
}

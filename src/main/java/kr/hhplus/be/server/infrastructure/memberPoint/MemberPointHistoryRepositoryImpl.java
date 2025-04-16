package kr.hhplus.be.server.infrastructure.memberPoint;

import kr.hhplus.be.server.domain.memberPoint.MemberPointHistory;
import kr.hhplus.be.server.domain.memberPoint.MemberPointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MemberPointHistoryRepositoryImpl implements MemberPointHistoryRepository {

    private final MemberPointHistoryJpaRepository memberPointHistoryJpaRepository;
    @Override
    public MemberPointHistory save(MemberPointHistory history) {
        return memberPointHistoryJpaRepository.save(history);
    }

    @Override
    public Optional<MemberPointHistory> findByMemberId(Long memberId) {
        return memberPointHistoryJpaRepository.findByMemberId(memberId);
    }
}

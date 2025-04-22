package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.coupon.CouponItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CouponItemRepositoryImpl implements CouponItemRepository {

    private final CouponItemJpaRepository couponItemJpaRepository;

    @Override
    public List<CouponItem> findAllByMemberId(Long memberId) {
        return couponItemJpaRepository.findAllByMemberId(memberId);
    }

    @Override
    public Optional<CouponItem> findById(Long id) {
        return couponItemJpaRepository.findById(id);
    }

    @Override
    public Optional<CouponItem> findByIdForUpdate(Long id) {
        return couponItemJpaRepository.findByIdForUpdate(id);
    }

    @Override
    public CouponItem save(CouponItem couponItem) {
        return couponItemJpaRepository.save(couponItem);
    }
}

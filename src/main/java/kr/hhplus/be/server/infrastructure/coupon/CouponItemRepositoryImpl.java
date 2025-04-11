package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.coupon.CouponItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CouponItemRepositoryImpl implements CouponItemRepository {

    @Override
    public List<CouponItem> findAllByMemberId(Long memberId) {
        return null;
    }

    @Override
    public Optional<CouponItem> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public CouponItem save(CouponItem couponItem) {
        return null;
    }
}

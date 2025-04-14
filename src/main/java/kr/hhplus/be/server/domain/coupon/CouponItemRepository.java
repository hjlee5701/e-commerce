package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface CouponItemRepository {
    List<CouponItem> findAllByMemberId(Long memberId);

    Optional<CouponItem> findById(Long id);

    CouponItem save(CouponItem couponItem);
}

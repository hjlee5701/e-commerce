package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponItemRepository {
    List<CouponItem> findAllByMemberId(Long memberId);

    Optional<CouponItem> findById(Long id);

    CouponItem save(CouponItem couponItem);
}

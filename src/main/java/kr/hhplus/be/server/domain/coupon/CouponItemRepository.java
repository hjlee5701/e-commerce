package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponItemRepository {
    List<CouponItem> findAllByMemberId(Long memberId);
}

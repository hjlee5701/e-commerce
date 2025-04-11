package kr.hhplus.be.server.domain.coupon;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository {
    Optional<Coupon> findById(Long couponId);
}

package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CouponRepositoryImpl implements CouponRepository {

    @Override
    public Optional<Coupon> findById(Long couponId) {
        return Optional.empty();
    }
}

package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponRedisRepository couponRedisRepository;
    @Override
    public Optional<Coupon> findByIdForUpdate(Long couponId) {
        return couponJpaRepository.findByIdForUpdate(couponId);
    }

    @Override
    public Coupon save(Coupon coupon) {
        return couponJpaRepository.save(coupon);
    }

    @Override
    public boolean isDuplicate(String couponIssuedSet, String userId) {
        return couponRedisRepository.isDuplicate(couponIssuedSet, userId);
    }

    @Override
    public boolean request(String couponIssuedSet, String memberId, Double score) {
        return couponRedisRepository.request(couponIssuedSet, memberId, score);
    }

    @Override
    public List<Coupon> getAllAvailable() {
        return couponJpaRepository.getAllAvailable();
    }

    @Override
    public String findOldestMemberByCouponId(String couponRequestKey) {
        return couponRedisRepository.findOldestMemberByCouponId(couponRequestKey);
    }

    @Override
    public void removeMemberInCouponRequest(String couponRequestKey, String memberId) {
        couponRedisRepository.removeMemberInCouponRequest(couponRequestKey, memberId);
    }

    @Override
    public void issue(String couponIssuedSet, String memberId) {
        couponRedisRepository.issue(couponIssuedSet, memberId);
    }

}

package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findByIdForUpdate(Long couponId);

    Coupon save(Coupon coupon);

    boolean isDuplicate(String couponIssuedSet, String memberId);

    boolean request(String couponIssuedSet, String memberId, Double score);

    List<Coupon> getAllAvailable();

    String findOldestMemberByCouponId(String couponRequestKey);

    void removeMemberInCouponRequest(String couponRequestKey, String memberId);

    void issue(String couponIssuedSet, String memberId);
}

package kr.hhplus.be.server.infrastructure.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CouponRedisRepository {
    private final RedisTemplate<String, String> redisTemplate;

    /*

    Key: "coupon:requests:{couponId}"
    Sorted Set:
        score=1684200000000, member="user123"
        score=1684200005000, member="user456"
     */

    // userId(member) 가 couponIssuedSet에 이미 존재하면 true 반환 (중복)
    public boolean isDuplicate(String couponIssuedSet, String memberId) {
        Boolean isDuplicate = redisTemplate.opsForSet().isMember(couponIssuedSet, memberId);
        return isDuplicate != null && isDuplicate;
    }

    public boolean request(String couponRequestKey, String memberId, double score) {
        return redisTemplate.opsForZSet().add(couponRequestKey, memberId, score);
    }

    public String findOldMembersByCouponId(String couponRequestKey, int count) {
        Set<String> oldestUsers = redisTemplate.opsForZSet().range(couponRequestKey, 0, count-1);
        return oldestUsers.isEmpty() ? null : oldestUsers.iterator().next();
    }


    public void removeMemberInCouponRequest(String couponRequestKey, String memberId) {
        redisTemplate.opsForZSet().remove(couponRequestKey, memberId);
    }

    public void issue(String couponIssuedSet, String memberId) {
        redisTemplate.opsForSet().add(couponIssuedSet, memberId);
    }
}

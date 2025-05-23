package kr.hhplus.be.server.infrastructure.orderStatistics;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class OrderStatisticsRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;


    public boolean incrementProductScore(String key, Long productId, Integer quantity) {
        // Redis의 ZSet에 판매량을 누적 (score = 판매량)
        try {
            Double newScore = redisTemplate.opsForZSet().incrementScore(key, productId.toString(), quantity);

            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

            if (ttl == null || ttl == -1) {
                redisTemplate.expire(key, 3, TimeUnit.DAYS);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    // 인기 상품 상위 topN 개 조회
    public Set<ZSetOperations.TypedTuple<String>> getTopProducts(String key, int topN) {

        return redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, topN-1);
    }


    public void aggregateTopProductsByPeriod(String rankingKey, List<String> dailyKeys) {

        // 3일치 ZSet 합산 (ZUNIONSTORE)
        redisTemplate.opsForZSet().unionAndStore(dailyKeys.get(0), dailyKeys.subList(1, dailyKeys.size()), rankingKey);

        // TTL 5일 설정
        redisTemplate.expire(rankingKey, Duration.ofDays(5));
    }
}

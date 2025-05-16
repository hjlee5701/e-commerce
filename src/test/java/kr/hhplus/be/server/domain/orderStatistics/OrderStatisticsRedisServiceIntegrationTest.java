package kr.hhplus.be.server.domain.orderStatistics;

import kr.hhplus.be.server.domain.statistics.OrderStatisticsCommand;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static kr.hhplus.be.server.common.FixtureTestSupport.FIXED_NOW;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Transactional
public class OrderStatisticsRedisServiceIntegrationTest {

    @Autowired
    private OrderStatisticsService orderStatisticsService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    @DisplayName("Redis에 일별 주문 통계 집계가 정상적으로 수행되는지 검증")
    void 일별주문통계_Redis_집계_통합테스트() {
        // given
        List<OrderStatisticsCommand.AggregateItem> itemsCommand = List.of(
                new OrderStatisticsCommand.AggregateItem(1L, 1),
                new OrderStatisticsCommand.AggregateItem(2L, 3)
        );

        LocalDate startDate = FIXED_NOW.toLocalDate();
        LocalDate endDate = startDate.plusDays(1);
        OrderStatisticsCommand.Aggregate command
                = new OrderStatisticsCommand.Aggregate(startDate, endDate, itemsCommand);

        String redisKey = "product:ranking:daily:" + endDate.toString();

        // when
        orderStatisticsService.dailyOrderStatistics(command);

        // then
        Double score1 = redisTemplate.opsForZSet().score(redisKey, "1");
        Double score2 = redisTemplate.opsForZSet().score(redisKey, "2");

        assertEquals(1.0, score1);
        assertEquals(3.0, score2);
    }

    @Test
    @DisplayName("3일치 상품 순위 합산 및 TTL 설정이 정상적으로 수행되는지 검증")
    void 상품순위_3일합산_및_TTL_설정_통합테스트() {
        // given - 테스트용 dailyKeys 셋업
        String key1 = "product:ranking:daily:20250513";
        String key2 = "product:ranking:daily:20250514";
        String key3 = "product:ranking:daily:20250515";

        String product1 = "1";
        String product2 = "2";
        String product3 = "3";
        redisTemplate.opsForZSet().add(key1, product1, 10);
        redisTemplate.opsForZSet().add(key1, product2, 20);

        redisTemplate.opsForZSet().add(key2, product1, 30);
        redisTemplate.opsForZSet().add(key2, product3, 40);

        redisTemplate.opsForZSet().add(key3, product2, 15);
        redisTemplate.opsForZSet().add(key3, product3, 25);

        List<String> dailyKeys = Arrays.asList(key1, key2, key3);
        String rankingKey = "product:ranking:3Days:20250515";

        // when
        orderStatisticsService.aggregateTopProductsByPeriod(rankingKey, dailyKeys);

        // then
        Set<ZSetOperations.TypedTuple<String>> result = redisTemplate.opsForZSet().rangeWithScores(rankingKey, 0, -1);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        // 예: product1, product2, product3 합산 점수 확인
        Map<String, Double> scores = result.stream()
                .collect(Collectors.toMap(ZSetOperations.TypedTuple::getValue, ZSetOperations.TypedTuple::getScore));

        assertEquals(40.0, scores.get(product1)); // 10 + 30
        assertEquals(35.0, scores.get(product2)); // 20 + 15
        assertEquals(65.0, scores.get(product3)); // 40 + 25

        // TTL 확인
        Long ttl = redisTemplate.getExpire(rankingKey);
        assertTrue(ttl > 0 && ttl <= Duration.ofDays(3).getSeconds());
    }

    @Test
    @DisplayName("결제된 상품별 판매량 Redis 집계가 정상 수행되는지 검증")
    void 결제상품_판매량_Redis_집계_통합테스트() {
        // given
        LocalDateTime startDateTime = FIXED_NOW;
        String redisKey = "product:ranking:" + startDateTime.toString();

        redisTemplate.opsForZSet().add(redisKey, "1", 5);
        redisTemplate.opsForZSet().add(redisKey, "2", 3);

        Map<Long, Integer> soldProductMap = Map.of(
                1L, 5,
                2L, 3
        );
        OrderStatisticsCommand.AggregatePaidProduct command =
                new OrderStatisticsCommand.AggregatePaidProduct(startDateTime, soldProductMap);


        // when
        orderStatisticsService.paidProductAggregateWithRedis(command);

        // then
        Double score1 = redisTemplate.opsForZSet().score(redisKey, String.valueOf(1L));
        Double score2 = redisTemplate.opsForZSet().score(redisKey, String.valueOf(2L));

        assertEquals(5.0, score1);
        assertEquals(3.0, score2);
    }

}
package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.support.TestDataFactory;
import kr.hhplus.be.server.support.TestDataManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static java.lang.Long.parseLong;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CouponServiceRedisIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponItemRepository couponItemRepository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CouponRepository couponRepository;
    private static final String COUPON_REQUEST_ZSET = "coupon:requests:";
    private static final String COUPON_ISSUED_SET = "coupon:issued:";

    @Autowired
    private TestDataFactory factory;

    @Autowired
    private TestDataManager testDataManager;

    @Test
    @DisplayName("Redis에 쿠폰 발급이 정상적으로 수행되는지 검증")
    void 쿠폰_발급_Redis_통합테스트() {
        // 1. 테스트용 쿠폰 생성 및 저장
        LocalDateTime now = LocalDateTime.now();
        Member member = factory.createMember();
        Coupon coupon = factory.createCoupon(100, now);
        testDataManager.persist(member);
        testDataManager.persist(coupon);
        String memberId = member.getId().toString();

        // 2. Redis에 요청자 추가 (가장 오래된 사용자 셋팅)
        String couponRequestKey = COUPON_REQUEST_ZSET + coupon.getId();
        Double score = (double) LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        couponRepository.request(couponRequestKey, memberId, score);

        // 3. 중복 이슈 없도록 초기화 (발급된 쿠폰셋 비워둠)
        String couponIssuedSet = COUPON_ISSUED_SET + coupon.getId();

        // 4. processCoupon 실행
        couponService.processCoupon(coupon, 2);

        // 5. 검증
        boolean isDuplicate = couponRepository.isDuplicate(couponIssuedSet, memberId);
        assertTrue(isDuplicate);

        Boolean oldestUsers = redisTemplate.opsForSet().isMember(couponIssuedSet, memberId);
        assertTrue(oldestUsers);

        List<CouponItem> couponItems = couponItemRepository.findAllByMemberId(parseLong(memberId));
        assertEquals(couponItems.size(), 1);
    }
}

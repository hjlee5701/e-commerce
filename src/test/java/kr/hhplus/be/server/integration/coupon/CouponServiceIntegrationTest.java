package kr.hhplus.be.server.integration.coupon;

import kr.hhplus.be.server.domain.coupon.*;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Testcontainers
@Transactional
public class CouponServiceIntegrationTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;


    @Test
    @DisplayName("쿠폰 발급 성공 통합 테스트")
    void 쿠폰_발급_통합_테스트(){
        // given
        int remainingQuantity = 10;
        Coupon coupon = new Coupon(null, "선착순 쿠폰", 100, remainingQuantity, BigDecimal.TEN, CouponStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        couponRepository.save(coupon);

        CouponCommand.Issue command = new CouponCommand.Issue(coupon.getId(), 100L);

        // when
        CouponInfo.Issued info = couponService.issue(command);

        // then
        assertThat(info).isNotNull();
        assertAll("발급된 쿠폰 정보 확인",
                () -> assertThat(info.getCouponItemId()).isNotNull(),
                () -> assertThat(info.getCouponId()).isEqualTo(coupon.getId()),
                () -> assertThat(info.getTitle()).isEqualTo(coupon.getTitle())
        );
        assertThat(coupon.getRemainingQuantity())
                .as("잔여 수량은 1 감소해야 함")
                .isEqualTo(remainingQuantity - 1);
    }

}

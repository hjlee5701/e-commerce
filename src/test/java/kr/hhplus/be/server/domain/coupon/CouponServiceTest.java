package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.CouponErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {
    @Mock
    private CouponItemRepository couponItemRepository;

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("존재하지 않는 쿠폰 조회시 예외가 발생한다.")
    void 존재하지_않는_쿠폰_조회_시_예외() {

        // given
        given(couponItemRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.findByCouponItemId(new CouponCommand.Find(1L)))
                .isInstanceOf(ECommerceException.class)
                .hasMessageContaining(CouponErrorCode.COUPON_ITEM_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("발급 유효성 검증 중, 쿠폰이 존재하지 않으면 예외가 발생한다.")
    void 쿠폰이_존재하지_않으면_예외_발생() {
        // given
        long couponId = 2L;
        CouponCommand.Issuable command = new CouponCommand.Issuable(couponId);

        given(couponRepository.findById(couponId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> couponService.issuable(command))
                .isInstanceOf(ECommerceException.class);
    }

}

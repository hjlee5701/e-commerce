package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.interfaces.code.CouponErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {
    @Mock
    private CouponItemRepository couponItemRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("존재하지 않는 쿠폰 조회시 예외가 발생한다.")
    void 존재하지_않는_쿠폰_조회_시_예외() {

        // given
        given(couponItemRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.findByCouponItemId(new CouponCommand.Find(1L)))
                .isInstanceOf(CouponItemNotFoundException.class)
                .hasMessageContaining(CouponErrorCode.COUPON_ITEM_NOT_FOUND.getMessage());


    }

}

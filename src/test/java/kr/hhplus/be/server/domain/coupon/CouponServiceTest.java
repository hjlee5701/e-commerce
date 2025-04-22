package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.CouponErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static kr.hhplus.be.server.common.FixtureTestSupport.ANY_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
        given(couponItemRepository.findByIdForUpdate(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.findByCouponItemId(new CouponCommand.Find(1L)))
                .isInstanceOf(ECommerceException.class)
                .hasMessageContaining(CouponErrorCode.COUPON_ITEM_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("쿠폰 발급 요청시 발급된 쿠폰은 저장된다.")
    void 쿠폰_발급시_쿠폰_아이템이_저장() {
        // given
        Coupon coupon = mock(Coupon.class);
        CouponItem couponItem = new CouponItemFixture().create();

        CouponCommand.Issue command = new CouponCommand.Issue(coupon.getId(), ANY_MEMBER_ID);

        given(couponRepository.findByIdForUpdate(coupon.getId())).willReturn(Optional.of(coupon));
        given(coupon.issue(any(), any())).willReturn(couponItem);
        given(couponItemRepository.save(couponItem)).willReturn(couponItem);

        // when
        couponService.issue(command);

        // then
        ArgumentCaptor<CouponItem> captor = ArgumentCaptor.forClass(CouponItem.class);
        verify(couponItemRepository, times(1)).save(captor.capture());
    }

}

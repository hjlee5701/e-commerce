package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.coupon.CouponItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouponItemRepositoryImpl implements CouponItemRepository {

    @Override
    public List<CouponItem> findAllByMemberId(Long memberId) {
        return null;
    }
}

package kr.hhplus.be.server.infrastructure.coupon;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponItemJpaRepository extends JpaRepository<CouponItem, Long> {
    List<CouponItem> findAllByMemberId(Long memberId);
}

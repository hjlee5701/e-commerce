package kr.hhplus.be.server.infrastructure.coupon;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.CouponItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponItemJpaRepository extends JpaRepository<CouponItem, Long> {
    List<CouponItem> findAllByMemberId(Long memberId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ci from CouponItem ci where ci.id = :id")
    Optional<CouponItem> findByIdForUpdate(Long id);
}

package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.application.coupon.CouponCriteria;
import kr.hhplus.be.server.shared.exception.ECommerceException;
import kr.hhplus.be.server.shared.code.CouponErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponItemRepository couponItemRepository;
    private final CouponRepository couponRepository;
    private static final String COUPON_REQUEST_ZSET = "coupon:requests:";
    private static final String COUPON_ISSUED_SET = "coupon:issued:";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public List<CouponInfo.ItemDetail> findHoldingCoupons(CouponCommand.Holdings command) {

        List<CouponItem> coupons = couponItemRepository.findAllByMemberId(command.getMemberId()); // fetch join

        return Optional.ofNullable(coupons)
                .orElse(Collections.emptyList())
                .stream()
                .map(CouponInfo.ItemDetail::of)
                .collect(Collectors.toList());
    }

    public CouponItem findByCouponItemId(CouponCommand.Find command) {
        return couponItemRepository.findByIdForUpdate(command.getCouponItemId())
                .orElseThrow(() -> new ECommerceException(CouponErrorCode.COUPON_ITEM_NOT_FOUND));
    }

    public CouponInfo.Issued issue(CouponCommand.Issue command) {
        Coupon coupon = couponRepository.findByIdForUpdate(command.getCouponId())
                .orElseThrow(() -> new ECommerceException(CouponErrorCode.COUPON_NOT_FOUND));

        // 쿠폰 발급
        CouponItem couponItem = coupon.issue(LocalDateTime.now(), command.getMemberId());

        // 발급 쿠폰 저장
        CouponItem savedCouponItem = couponItemRepository.save(couponItem);

        return CouponInfo.Issued.of(coupon, savedCouponItem);
    }

    // 요청 등록 (중복 체크 및 Sorted Set에 시간 기준 저장)

    /**
     * 사용자 요청 : 쿠폰 발급
     */
    public boolean requestCoupon(String couponId, String memberId) {
        // 중복 확인 (발급된 적 있는지)
        String couponRequestKey = COUPON_REQUEST_ZSET + couponId; // 쿠폰별 요청 Sorted Set 키

        // Sorted Set에 요청 시간 점수로 추가, userId를 멤버로 저장
        Double score = (double) LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        return couponRepository.request(couponRequestKey, memberId, score);
    }


    // 스케줄러에서 순차 발급 처리
    @Transactional
    public void processCoupon(Coupon coupon) {
        String couponRequestKey = COUPON_REQUEST_ZSET + coupon.getId();

        while (coupon.getRemainingQuantity() > 0) {
            String memberId = couponRepository.findOldestMemberByCouponId(couponRequestKey);
            if (memberId == null) {
                break;
            }
            var event = CouponEvent.Issued.of(coupon.getId(), memberId);
            int partition = (int)(coupon.getId() % 3);
            kafkaTemplate.send("COUPON", partition, String.valueOf(coupon.getId()), event);
        }
    }


    public List<Coupon> getAllAvailable() {
        return couponRepository.getAllAvailable();
    }

    public void issueV2(CouponCommand.IssueV2 command) {
        Long couponId = command.getCouponId();
        String memberId = command.getMemberId();
        String couponIssuedSet = COUPON_ISSUED_SET + couponId;
        String couponRequestKey = COUPON_REQUEST_ZSET + couponId;


        // 중복 확인 (발급 상태 체크)
        if (couponRepository.isDuplicate(couponIssuedSet, memberId)) {
            couponRepository.removeMemberInCouponRequest(couponRequestKey, memberId);
            return;
        }

        // 쿠폰 발급 (발급자 Set에 추가)
        couponRepository.issue(couponIssuedSet, memberId);

        // 요청에서 삭제
        couponRepository.removeMemberInCouponRequest(couponRequestKey, memberId);

        // DB 저장
        Coupon coupon = couponRepository.findByIdForUpdate(couponId).get();
        CouponItem couponItem = coupon.issue(LocalDateTime.now(), parseLong(memberId));
        couponItemRepository.save(couponItem);
    }
}

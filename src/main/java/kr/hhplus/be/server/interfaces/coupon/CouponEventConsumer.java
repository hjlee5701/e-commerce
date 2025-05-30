package kr.hhplus.be.server.interfaces.coupon;

import kr.hhplus.be.server.domain.coupon.CouponCommand;
import kr.hhplus.be.server.domain.coupon.CouponEvent;
import kr.hhplus.be.server.domain.coupon.CouponService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponEventConsumer {

    private final CouponService couponService;

    @KafkaListener(topics = "COUPON", groupId = "coupon-group", concurrency = "3"
    )
    public void handleCouponRequestEvent(ConsumerRecord<String, CouponEvent.Issued> record,
                                         Acknowledgment acknowledgment) {
        System.out.println("쿠폰 처리" + record.key() + record.value().getCouponId());

        couponService.issueV2(CouponCommand.IssueV2.of(record.value().getCouponId(), record.value().getMemberId()));
        acknowledgment.acknowledge();
    }

}
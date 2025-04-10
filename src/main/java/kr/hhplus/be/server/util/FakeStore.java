package kr.hhplus.be.server.util;

import kr.hhplus.be.server.domain.coupon.dto.CouponResponse;
import kr.hhplus.be.server.domain.memberPoint.dto.ChargePointResponse;
import kr.hhplus.be.server.domain.memberPoint.dto.MemberPointResponse;
import kr.hhplus.be.server.domain.order.dto.OrderItemResponse;
import kr.hhplus.be.server.domain.order.dto.OrderResponse;
import kr.hhplus.be.server.domain.order.dto.PopularProductResponse;
import kr.hhplus.be.server.domain.payment.dto.OrderPaymentResponse;
import kr.hhplus.be.server.domain.product.dto.ProductResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class FakeStore {

    public ChargePointResponse chargePoint() {
        return new ChargePointResponse(1L, BigDecimal.valueOf(20000), BigDecimal.valueOf(5000));
    }

    public MemberPointResponse memberPoint() {
        return new MemberPointResponse(1L, BigDecimal.valueOf(20000));
    }

    public ProductResponse product() {
        return new ProductResponse(1L, "상의", BigDecimal.valueOf(10000), 200);
    }

    public CouponResponse coupon() {
        return new CouponResponse(1L, 50L, "선착순 할인 쿠폰", LocalDateTime.now(), LocalDateTime.now().plusDays(7), "ISSUED");
    }

    public List<CouponResponse> couponList() {
        return List.of(
                new CouponResponse(1L, 50L, "선착순 할인 쿠폰A", LocalDateTime.now(), LocalDateTime.now().plusDays(7), "ISSUED"),
                new CouponResponse(2L, 10L, "선착순 할인 쿠폰B", LocalDateTime.now(), LocalDateTime.now().plusDays(7), "USED")
        );
    }

    public OrderResponse order() {
        return new OrderResponse(
                1L, "COMPLETED", BigDecimal.valueOf(50000), LocalDateTime.now(),
                List.of(
                        new OrderItemResponse(1L, "상의", BigDecimal.valueOf(50000), 1)
                ));
    }

    public List<PopularProductResponse> popularProducts() {
        return List.of(
                new PopularProductResponse(1, 1L, "상의", BigDecimal.valueOf(10000), 300, LocalDateTime.now())
        );
    }

    public OrderPaymentResponse orderPayment() {
        return new OrderPaymentResponse(1L, 1L, BigDecimal.valueOf(50000), BigDecimal.valueOf(10000), BigDecimal.valueOf(40000), "COMPLETED");
    }
}

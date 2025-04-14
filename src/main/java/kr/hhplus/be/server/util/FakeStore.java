package kr.hhplus.be.server.util;

import kr.hhplus.be.server.interfaces.coupon.CouponResponse;
import kr.hhplus.be.server.interfaces.memberPoint.MemberPointResponse;
import kr.hhplus.be.server.interfaces.order.OrderResponse;
import kr.hhplus.be.server.interfaces.payment.PaymentResponse;
import kr.hhplus.be.server.interfaces.product.ProductResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class FakeStore {

    public MemberPointResponse.ChargeBalance balance() {
        return new MemberPointResponse.ChargeBalance(1L, BigDecimal.valueOf(20000));
    }

    public ProductResponse.Detail product() {
        return new ProductResponse.Detail(1L, "상의", BigDecimal.valueOf(10000), 200);
    }

    public CouponResponse.Issued coupon() {

        return new CouponResponse.Issued(1L, 50L, "선착순 할인 쿠폰", LocalDateTime.now(), LocalDateTime.now().plusDays(7), "ISSUED");
    }

    public List<CouponResponse.Issued> couponList() {
        return List.of(
                new CouponResponse.Issued(1L, 50L, "선착순 할인 쿠폰A", LocalDateTime.now(), LocalDateTime.now().plusDays(7), "ISSUED"),
                new CouponResponse.Issued(2L, 10L, "선착순 할인 쿠폰B", LocalDateTime.now(), LocalDateTime.now().plusDays(7), "USED")
        );
    }

    public OrderResponse.Created ordered() {
        return new OrderResponse.Created(
                1L, "COMPLETED", BigDecimal.valueOf(50000), LocalDateTime.now(),
                List.of(
                        new OrderResponse.ItemCreated(1L, "상의", BigDecimal.valueOf(10000), BigDecimal.valueOf(50000), 1)
                ));
    }

    public List<ProductResponse.Popular> popularProducts() {
        return List.of(
                new ProductResponse.Popular(1, 1L, "상의", BigDecimal.valueOf(10000), 300, LocalDateTime.now())
        );
    }

    public PaymentResponse.Paid orderPayment() {
        return new PaymentResponse.Paid(1L, 1L, BigDecimal.valueOf(50000), BigDecimal.valueOf(10000), BigDecimal.valueOf(40000), "COMPLETED");
    }
}

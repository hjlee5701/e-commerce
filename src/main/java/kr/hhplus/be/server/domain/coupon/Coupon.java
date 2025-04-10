package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
public class Coupon {
    @Id
    @GeneratedValue
    @Column(name = "COUPON_ID")
    private Long id;

    private String title;

    private int initialQuantity;

    private int remainingQuantity;

    private BigDecimal discountAmount;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    LocalDateTime issuedAt;

    LocalDateTime expiredAt;

}

package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.coupon.CouponItem;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.product.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
public class OrderCommand {

    @Getter
    @AllArgsConstructor
    public static class Create {
        private Member member;
        private CouponItem couponItem;
        private List<ItemCreate> orderItems;
        private BigDecimal totalAmount;
    }

    @Getter
    @AllArgsConstructor
    public static class ItemCreate {
        private Product product;
        private BigDecimal price;
        private int quantity;
    }
}

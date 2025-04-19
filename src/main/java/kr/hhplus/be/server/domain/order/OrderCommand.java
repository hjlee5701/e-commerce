package kr.hhplus.be.server.domain.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class OrderCommand {

    @Getter
    @AllArgsConstructor
    public static class Create {
        private Long memberId;
        private List<ItemCreate> products; // 조회한 상품 정보
        Map<Long, Integer> orderProductMap;// 주문 요청한 상품의 ID 와 수량
    }

    @Getter
    @AllArgsConstructor
    public static class ItemCreate {
        private Long productId;
        private String title;
        private BigDecimal price;
        private Integer quantity;
    }

    @Getter
    @AllArgsConstructor
    public static class Find {
        private Long orderId;
    }

}

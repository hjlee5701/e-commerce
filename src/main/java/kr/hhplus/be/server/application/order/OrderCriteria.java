package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.member.MemberCommand;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.interfaces.order.OrderRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OrderCriteria {

    @Getter
    @AllArgsConstructor
    public static class Create {
        private Long memberId;
        private List<ItemCreate> orderItems;

        public static Create of(Long memberId, OrderRequest.Create request) {
            List<ItemCreate> itemCriteria = request.getOrderItems().stream().map(
                    product -> new ItemCreate(product.getProductId(), product.getOrderQuantity())
            ).toList();
            return new Create(
                    memberId, itemCriteria
            );
        }

        public MemberCommand.Find toFindMemberCommand() {
            return new MemberCommand.Find(memberId);
        }


        public OrderCommand.Create toCreateOrderCommand(List<ProductInfo.Detail> productInfo) {
            // 조회한 상품들의 정보
            List<OrderCommand.ItemCreate> products = productInfo.stream()
                    .map(info -> new OrderCommand.ItemCreate(
                            info.getProductId(),
                            info.getTitle(),
                            info.getPrice(),
                            info.getQuantity()))
                    .toList();

            Map<Long, Integer> orderProductMap = orderItems.stream()// 주문 요청한 상품의 ID 및 수량
                    .collect(Collectors.toMap(
                            ItemCreate::getProductId,
                            ItemCreate::getOrderQuantity
                    ));

            return new OrderCommand.Create(memberId, products, orderProductMap);
        }

        public ProductCommand.FindAll toFindProductsCommand() {
            Set<Long> productIds = orderItems.stream()
                    .map(ItemCreate::getProductId)
                    .collect(Collectors.toSet());
            return new ProductCommand.FindAll(productIds);
        }

    }

    @Getter
    @AllArgsConstructor
    public static class ItemCreate {
        private Long productId;
        private Integer orderQuantity;
    }

}

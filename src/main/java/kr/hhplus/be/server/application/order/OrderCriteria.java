package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.member.MemberCommand;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.interfaces.order.OrderRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderCriteria {

    @Getter
    @AllArgsConstructor
    public static class Create {
        private Long memberId;
        private List<ItemCreate> orderItems;

        public static Create of(Long memberId, OrderRequest.Create request) {
            List<ItemCreate> itemCriteria = request.getOrderItems().stream().map(
                    product -> new ItemCreate(product.getProductId(), product.getQuantity())
            ).toList();
            return new Create(
                    memberId, itemCriteria
            );
        }

        public MemberCommand.Find toFindMemberCommand() {
            return new MemberCommand.Find(memberId);
        }


        public ProductCommand.Decrease toDecreaseStockCommand() {
            Map<Long, Integer> productMap = orderItems.stream()
                    .collect(Collectors.toMap(
                            ItemCreate::getProductId,
                            ItemCreate::getQuantity
                    ));
            return new ProductCommand.Decrease(productMap);
        }

        public OrderCommand.Create toCreateOrderCommand(Member member, ProductInfo.Decreased productInfo) {
            List<OrderCommand.ItemCreate> itemCommands = productInfo.getItems().stream()
                    .map(info -> new OrderCommand.ItemCreate(
                            info.getProduct(),
                            info.getPrice(),
                            info.getOrderQuantity()))
                    .toList();

            return new OrderCommand.Create(member, itemCommands, productInfo.getTotalAmount());
        }

    }

    @Getter
    @AllArgsConstructor
    public static class ItemCreate {
        private Long productId;
        private Integer quantity;
    }

}

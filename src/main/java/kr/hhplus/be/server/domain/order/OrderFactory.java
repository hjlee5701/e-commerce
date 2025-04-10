package kr.hhplus.be.server.domain.order;

import java.time.LocalDateTime;

public class OrderFactory {

    public static Order create(OrderCommand.Create command) {
        return new Order(
                1L,
                command.getMember(),
                command.getTotalAmount(),
                OrderStatus.PENDING,
                LocalDateTime.now()
        );
    }

    public static OrderItem createItem(Order order, OrderCommand.ItemCreate command) {
        return new OrderItem(
                1L,
                command.getProduct().getTitle(),
                order,
                command.getProduct(),
                command.getPrice(),
                command.getQuantity()
        );
    }

}

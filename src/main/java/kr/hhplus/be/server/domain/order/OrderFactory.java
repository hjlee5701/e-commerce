package kr.hhplus.be.server.domain.order;

public class OrderFactory {

    public static Order create(OrderCommand.Create command) {
        return new Order(
                null,
                command.getMember(),
                command.getTotalAmount(),
                OrderStatus.PENDING,
                null
        );
    }

    public static OrderItem createItem(Order order, OrderCommand.ItemCreate command) {
        return new OrderItem(
                null,
                command.getProduct().getTitle(),
                order,
                command.getProduct(),
                command.getPrice(),
                command.getQuantity()
        );
    }

}

package kr.hhplus.be.server.domain.product;

public class ProductFactory {
    public static ProductStock createOutBoundStock(Product product, Integer orderQuantity) {
        return new ProductStock(1L, product, orderQuantity, StockType.OUTBOUND);
    }
}

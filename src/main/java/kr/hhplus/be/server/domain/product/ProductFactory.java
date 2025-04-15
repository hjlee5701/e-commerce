package kr.hhplus.be.server.domain.product;

public class ProductFactory {
    public static ProductStock createOutBoundStock(Product product, Integer orderQuantity) {
        return new ProductStock(null, product, orderQuantity, StockType.OUTBOUND);
    }
}

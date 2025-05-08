package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.shared.exception.ECommerceException;
import kr.hhplus.be.server.shared.code.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductStockService {
    private final ProductStockRepository productStockRepository;

    public void decreaseStock(ProductStockCommand.Decrease command) {
        command.getProductMap().forEach(this::decreaseStock);
    }

    private void decreaseStock(Long productId, Integer orderQuantity) {
        ProductStock stock = productStockRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() -> new ECommerceException(ProductErrorCode.PRODUCT_STOCK_NOT_FOUND));

        stock.decrease(orderQuantity);
    }

}

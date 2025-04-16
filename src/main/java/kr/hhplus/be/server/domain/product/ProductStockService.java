package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductStockService {
    private final ProductStockRepository productStockRepository;

    public void decreaseStock(ProductStockCommand.Decrease command) {
        command.getProductMap().forEach(this::deductStock);
    }

    private void deductStock(Long productId, Integer orderQuantity) {
        ProductStock stock = productStockRepository.findByProductId(productId)
                .orElseThrow(() -> new ECommerceException(ProductErrorCode.PRODUCT_NOT_FOUND));

        stock.decrease(orderQuantity);
    }

}

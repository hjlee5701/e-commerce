package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductStockRepository productStockRepository;

    public ProductInfo.Detail getProductDetail(ProductCommand.Detail command) {
        Product product = productRepository.findById(command.getProductId())
                .orElseThrow(() -> new ECommerceException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return ProductInfo.Detail.of(product);

    }


    public ProductInfo.Decreased decreaseStock(ProductCommand.Decrease command) {

        Map<Long, Integer> productMap = command.getProductMap();
        
        List<Product> products = productRepository.findAllById(productMap.keySet());

        // 상품 ID 유효성 검사
        if (products.size() != productMap.size()) {
            throw new ECommerceException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        List<ProductInfo.ItemDecreased> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Product product : products) {
            Integer orderQuantity = productMap.get(product.getId());
            
            // 수량 차감
            product.decrease(orderQuantity);
            items.add(ProductInfo.ItemDecreased.of(product, orderQuantity));

            // 상품 가격 합
            totalAmount = totalAmount.add(product.getPrice());

            // 재고 이력 저장
            ProductStock stock = ProductFactory.createOutBoundStock(product, orderQuantity);
            productStockRepository.save(stock);
        }

        return ProductInfo.Decreased.of(totalAmount, items);
    }
}

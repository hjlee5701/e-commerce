package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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


    public List<ProductInfo.Detail> decreaseStock(ProductCommand.Decrease command) {

        Map<Long, Integer> productMap = command.getProductMap();
        
        List<Product> products = productRepository.findAllById(productMap.keySet());

        // 상품 ID 유효성 검사
        if (products.size() != productMap.size()) {
            throw new ECommerceException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        List<ProductInfo.Detail> items = new ArrayList<>();

        for (Product product : products) {
            Integer orderQuantity = productMap.get(product.getId());
            
            // 수량 차감
            product.decrease(orderQuantity);
            items.add(ProductInfo.Detail.of(product));

            // 재고 이력 저장
            ProductStock stock = ProductFactory.createOutBoundStock(product, orderQuantity);
            productStockRepository.save(stock);
        }

        return items;
    }
}

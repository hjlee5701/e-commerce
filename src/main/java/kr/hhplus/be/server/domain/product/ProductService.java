package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.shared.exception.ECommerceException;
import kr.hhplus.be.server.shared.code.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductInfo.Detail getProductDetail(ProductCommand.Detail command) {
        Product product = productRepository.findById(command.getProductId())
                .orElseThrow(() -> new ECommerceException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return ProductInfo.Detail.of(product);

    }
    public List<ProductInfo.Detail> findProductsByIds(ProductCommand.FindAll command) {
        return productRepository.findAllById(command.getProductIds())
                .stream()
                .map(ProductInfo.Detail::of)
                .toList();
    }

    public List<ProductInfo.Detail> findPopularProductsByIds(List<Long> productIds) {
        return productIds.stream()
                .map(id -> productRepository.findById(id)
                        .map(ProductInfo.Detail::of)
                        .orElse(null))
                .toList();
    }

}

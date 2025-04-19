package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.interfaces.code.ProductErrorCode;
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

}

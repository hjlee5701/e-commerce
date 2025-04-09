package kr.hhplus.be.server.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductInfo.Detail getProductDetail(ProductCommand.Detail command) {
        Product product = productRepository.findById(command.getProductId())
                .orElseThrow(ProductNotFoundException::new);
        return ProductInfo.Detail.of(product);

    }


}

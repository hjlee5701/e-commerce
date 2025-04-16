package kr.hhplus.be.server.application;

import kr.hhplus.be.server.application.payment.ProductResult;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductFacade {

    private final ProductService productService;


    public List<ProductResult.Popular> popular() {
        // 통계에서 조회
        //
        /*



         */
        return null;
    }
}

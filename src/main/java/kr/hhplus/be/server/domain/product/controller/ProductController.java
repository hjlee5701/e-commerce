package kr.hhplus.be.server.domain.product.controller;

import kr.hhplus.be.server.domain.order.dto.PopularProductResponse;
import kr.hhplus.be.server.domain.product.dto.ProductResponse;
import kr.hhplus.be.server.global.response.ApiResult;
import kr.hhplus.be.server.global.response.SuccessCode;
import kr.hhplus.be.server.util.FakeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController implements ProductApi{

    private final FakeStore fakeStore;

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<ProductResponse>> findProduct (
            @PathVariable("id") Long productId
    ) {
        ProductResponse response = fakeStore.product();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.FIND_PRODUCT, response));
    }

    @Override
    @GetMapping("/popular")
    public ResponseEntity<ApiResult<List<PopularProductResponse>>> findPopularProducts() {
        List<PopularProductResponse> response = fakeStore.popularProducts();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.FIND_POPULAR_PRODUCT, response));
    }

}

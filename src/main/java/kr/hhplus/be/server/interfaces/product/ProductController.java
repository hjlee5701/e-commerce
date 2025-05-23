package kr.hhplus.be.server.interfaces.product;

import kr.hhplus.be.server.domain.product.ProductCommand;
import kr.hhplus.be.server.domain.product.ProductInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.shared.dto.ApiResult;
import kr.hhplus.be.server.shared.code.SuccessCode;
import kr.hhplus.be.server.support.fake.FakeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController implements ProductApi {

    private final FakeStore fakeStore;
    private final ProductService service;

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<ProductResponse.Detail>> findProduct (
            @PathVariable("id") Long productId
    ) {
        ProductInfo.Detail result = service.getProductDetail(new ProductCommand.Detail(productId));
        var data = ProductResponse.Detail.of(result);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.FIND_PRODUCT, data));
    }

}

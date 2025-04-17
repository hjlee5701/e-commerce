package kr.hhplus.be.server.interfaces.orderStatistics;

import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
import kr.hhplus.be.server.util.FakeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class OrderStatisticsController implements OrderStatisticsApi {

    private final FakeStore fakeStore;


    @Override
    @GetMapping("/products/popular")
    public ResponseEntity<ApiResult<List<OrderStatisticsResponse.Popular>>> findPopularProducts() {
        List<OrderStatisticsResponse.Popular> response = fakeStore.popularProducts();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.FIND_POPULAR_PRODUCT, response));
    }
}

package kr.hhplus.be.server.interfaces.orderStatistics;

import kr.hhplus.be.server.domain.statistics.OrderStatisticsInfo;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsService;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class OrderStatisticsController implements OrderStatisticsApi {

    private final OrderStatisticsService service;

    @Override
    @GetMapping("/products/popular")
    public ResponseEntity<ApiResult<List<OrderStatisticsResponse.Popular>>> findPopularProducts() {

        List<OrderStatisticsInfo.Popular> responses = service.popular();
        var data = Optional.ofNullable(responses)
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderStatisticsResponse.Popular::of)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.FIND_POPULAR_PRODUCT, data));
    }
}

package kr.hhplus.be.server.interfaces.orderStatistics;

import kr.hhplus.be.server.domain.statistics.OrderStatisticsCommand;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsInfo;
import kr.hhplus.be.server.domain.statistics.OrderStatisticsService;
import kr.hhplus.be.server.shared.code.SuccessCode;
import kr.hhplus.be.server.shared.dto.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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

        LocalDate now = LocalDate.now();
        OrderStatisticsCommand.Popular command = OrderStatisticsCommand.Popular.of(now.minusDays(3), now, 5);
        List<OrderStatisticsInfo.Popular> infos = service.popular(command);

        var data = Optional.ofNullable(infos)
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderStatisticsResponse.Popular::of)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.FIND_POPULAR_PRODUCT, data));
    }
}

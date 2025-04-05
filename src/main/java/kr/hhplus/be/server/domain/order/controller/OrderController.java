package kr.hhplus.be.server.domain.order.controller;

import kr.hhplus.be.server.domain.order.dto.OrderRequest;
import kr.hhplus.be.server.domain.order.dto.OrderResponse;
import kr.hhplus.be.server.global.response.ApiResult;
import kr.hhplus.be.server.global.response.SuccessCode;
import kr.hhplus.be.server.util.FakeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController implements OrderApi {

    private final FakeStore fakeStore;

    @Override
    @PostMapping("{id}")
    public ResponseEntity<ApiResult<OrderResponse>> order (
            @PathVariable("id") Long memberId,
            @RequestBody OrderRequest orderRequest
    ) {
        OrderResponse response = fakeStore.order();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.ORDER, response));
    }

}

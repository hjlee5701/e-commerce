package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
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
    public ResponseEntity<ApiResult<OrderResponse.Created>> order (
            @PathVariable("id") Long memberId,
            @RequestBody OrderRequest.Create orderRequest
    ) {
        OrderResponse.Created response = fakeStore.ordered();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.ORDER, response));
    }

}

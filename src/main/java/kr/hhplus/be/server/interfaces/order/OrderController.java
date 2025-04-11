package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.application.order.OrderCriteria;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.OrderResult;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController implements OrderApi {

    private final OrderFacade facade;

    @Override
    @PostMapping("{id}")
    public ResponseEntity<ApiResult<OrderResponse.Created>> order (
            @PathVariable("id") Long memberId,
            @RequestBody OrderRequest.Create orderRequest
    ) {
        var criteria = OrderCriteria.Create.of(memberId, orderRequest);

        OrderResult.Created result = facade.createOrder(criteria);
        var data = OrderResponse.Created.of(result);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.ORDER, data));
    }

}

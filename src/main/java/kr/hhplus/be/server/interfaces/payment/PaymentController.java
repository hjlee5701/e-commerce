package kr.hhplus.be.server.interfaces.payment;

import kr.hhplus.be.server.application.payment.PaymentCriteria;
import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.application.payment.PaymentResult;
import kr.hhplus.be.server.interfaces.common.ApiResult;
import kr.hhplus.be.server.interfaces.common.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PaymentController implements PaymentApi {

    private final PaymentFacade facade;

    @Override
    @PostMapping("/orders/{id}/payments")
    public ResponseEntity<ApiResult<PaymentResponse.Paid>> orderPayment(
            @PathVariable("id") Long orderId,
            @RequestBody PaymentRequest.Pay request
    ) {
        var criteria = PaymentCriteria.Pay.of(orderId, request);
        PaymentResult.Paid result = facade.createPayment(criteria);

        var data = PaymentResponse.Paid.of(result);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.ORDER_PAYMENT, data));
    }
}

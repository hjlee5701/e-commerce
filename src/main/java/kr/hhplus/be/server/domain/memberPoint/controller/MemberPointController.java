package kr.hhplus.be.server.domain.memberPoint.controller;

import kr.hhplus.be.server.domain.memberPoint.dto.ChargePointRequest;
import kr.hhplus.be.server.domain.memberPoint.dto.ChargePointResponse;
import kr.hhplus.be.server.global.response.ApiResult;
import kr.hhplus.be.server.global.response.SuccessCode;
import kr.hhplus.be.server.util.FakeStore;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/member-points")
public class MemberPointController implements MemberPointApi {

    private final FakeStore fakeStore;

    @Override
    @PatchMapping("{id}/charge")
    public ResponseEntity<ApiResult<ChargePointResponse>> charge(
            @PathVariable("id") Long memberId,
            @RequestBody ChargePointRequest chargePointRequest
    ) {
        ChargePointResponse data = fakeStore.chargePoint();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResult.of(SuccessCode.CHARGE, data));
    }

}


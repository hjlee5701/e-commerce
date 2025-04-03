package kr.hhplus.be.server.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements Code {
    CHARGE("200", "충전에 성공했습니다."),
    BALANCE_CHECK("200","보유 금액 조회 성공했습니다."),
    FIND_PRODUCT("200","상품 목록 조회에 성공했습니다."),
    ISSUE_COUPON("200","쿠폰 발급 성공했습니다."),
    CHECK_HOLDING_COUPON("200","쿠폰 목록 조회 성공했습니다."),
    ORDER("200","주문 성공했습니다."),

    ;
    private final String code;
    private final String message;

}

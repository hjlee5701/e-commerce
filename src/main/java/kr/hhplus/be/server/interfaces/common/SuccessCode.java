package kr.hhplus.be.server.interfaces.common;

import kr.hhplus.be.server.interfaces.code.Code;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode implements Code {
    CHARGE("SUCCESS", "충전에 성공했습니다."),
    BALANCE_CHECK("SUCCESS","보유 금액 조회 성공했습니다."),
    FIND_PRODUCT("SUCCESS","상품 목록 조회에 성공했습니다."),
    ISSUE_COUPON("SUCCESS","쿠폰 발급 성공했습니다."),
    FIND_HOLDING_COUPON("SUCCESS","쿠폰 목록 조회 성공했습니다."),
    ORDER("SUCCESS","주문 성공했습니다."),
    FIND_POPULAR_PRODUCT("SUCCESS","인기 상품 목록 조회 성공했습니다."),
    ORDER_PAYMENT("SUCCESS", "주문 결제에 성공했습니다."),

    ;
    private final String code;
    private final String message;

}

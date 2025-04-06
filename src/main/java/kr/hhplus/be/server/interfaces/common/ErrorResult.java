package kr.hhplus.be.server.interfaces.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResult {
    private String code;
    private String message;

    public static ErrorResult of(Code code) {
        return new ErrorResult(code.getCode(), code.getMessage());
    }
}

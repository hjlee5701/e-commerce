package kr.hhplus.be.server.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResult<T> {

    @Schema(example = "200")
    private String code;
    @Schema(example = "성공 메시지")
    private String message;

    private T data;

    public static <T> ApiResult<T> of(Code code, T content) {
        return new ApiResult<>(code.getCode(), code.getMessage(), content);
    }
}
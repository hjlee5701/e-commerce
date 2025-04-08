package kr.hhplus.be.server.interfaces.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Tag(name = "공통 응답 바디 DTO")
public class ApiResult<T> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(title = "응답 코드", example = "SUCCESS", description = "성공 응답에만 반환되는 값입니다.")
    private String code;

    @Schema(example = "성공/에러 메시지")
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @Schema(hidden = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorCode;

    public static <T> ApiResult<T> of(Code code, T content) {
        return new ApiResult<>(code.getCode(), code.getMessage(), content, null);
    }

    public static <T> ApiResult<T> of(String message, String errorCode) {
        return new ApiResult<>(null, message, null, errorCode);
    }
}
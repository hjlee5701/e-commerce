package kr.hhplus.be.server.domain.memberPoint.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Schema(title = "사용자 보유 금액 조회 응답")
public class MemberPointResponse {

    @Schema(title = "사용자 아이디", example = "1")
    private Long memberId;

    @Schema(title = "보유 금액", example = "20000")
    private BigDecimal balance;
}

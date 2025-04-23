package kr.hhplus.be.server.domain.memberPoint;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.common.ECommerceException;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.interfaces.code.MemberPointErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static kr.hhplus.be.server.domain.memberPoint.MemberPointPolicy.MAX_CHARGE_AMOUNT;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class MemberPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_POINT_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    private BigDecimal balance;

    @Version
    private Long version;

    public static MemberPoint createInitialPoint(Long memberId) {
        return new MemberPoint(null, Member.referenceById(memberId), BigDecimal.ZERO, null);
    }

    public void charge(BigDecimal amount) {
        this.balance = this.balance.add(amount);
        validateAmount(amount);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.add(balance).compareTo(MAX_CHARGE_AMOUNT) > 0) {
            throw new ECommerceException(MemberPointErrorCode.INVALID_BALANCE);
        }
    }

    public void use(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        if (balance.compareTo(amount) < 0) {
            throw new ECommerceException(MemberPointErrorCode.INSUFFICIENT_BALANCE);
        }
        balance = balance.subtract(amount);
    }
}
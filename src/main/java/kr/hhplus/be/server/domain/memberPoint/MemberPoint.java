package kr.hhplus.be.server.domain.memberPoint;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.member.Member;
import kr.hhplus.be.server.domain.memberPoint.exception.InvalidBalanceException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static kr.hhplus.be.server.domain.memberPoint.MemberPointPolicy.*;

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

    public void charge(MemberPointCommand.Charge command) {
        this.balance = this.balance.add(command.getAmount());
        validateAmount(command.getAmount());
        this.member = command.getMember();
    }

    private void validateAmount(BigDecimal amount) {
        if (amount.add(balance).compareTo(MAX_CHARGE_AMOUNT) > 0) {
            throw new InvalidBalanceException();
        }
    }
}
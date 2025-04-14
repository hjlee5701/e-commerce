package kr.hhplus.be.server.domain.memberPoint;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class MemberPointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_POINT_HISTORY_ID")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private BigDecimal amount;

    public static MemberPointHistory createChargeHistory(Long memberId, BigDecimal amount) {
        return new MemberPointHistory(null, Member.referenceById(memberId), TransactionType.CHARGE, amount);
    }
}

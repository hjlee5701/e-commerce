package kr.hhplus.be.server.domain.memberPoint;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    private LocalDateTime createdAt;

    public static MemberPointHistory createChargeHistory(Long memberId, BigDecimal amount) {
        return new MemberPointHistory(null, Member.referenceById(memberId), TransactionType.CHARGE, amount, null);
    }

    public static MemberPointHistory createUseHistory(Long memberId, BigDecimal amount) {
        return new MemberPointHistory(null, Member.referenceById(memberId), TransactionType.USE, amount, null);
    }
}

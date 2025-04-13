package kr.hhplus.be.server.domain.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    private String userId;

    private LocalDateTime regAt;

    private Member(Long memberId) {
        this.id = memberId;
    }

    public static Member referenceById(Long memberId) {
        return new Member(memberId);
    }

}

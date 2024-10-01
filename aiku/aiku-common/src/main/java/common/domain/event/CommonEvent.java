package common.domain.event;

import common.domain.BaseTime;
import common.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
public abstract class CommonEvent extends BaseTime {
    @Column(name = "eventId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "memberId")
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    public CommonEvent(Member member) {
        this.member = member;
    }
}

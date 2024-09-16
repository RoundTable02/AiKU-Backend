package common.domain;

import common.domain.member.Member;
import common.domain.value_reference.ScheduleMemberValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Betting extends BaseTime{

    @Column(name = "bettingId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AttributeOverride(name = "id", column = @Column(name = "bettorScheduleMemberId"))
    @Embedded
    private ScheduleMemberValue bettor;

    @AttributeOverride(name = "id", column = @Column(name = "beteeScheduleMemberId"))
    @Embedded
    private ScheduleMemberValue betee;

    @AttributeOverride(name = "id", column = @Column(name = "winnerScheduleMemberId"))
    @Embedded
    private ScheduleMemberValue winner;

    @Enumerated(value = EnumType.STRING)
    private ExecStatus bettingStatus = ExecStatus.WAIT;

    private int pointAmount;

    @Enumerated(value = EnumType.STRING)
    private Status status = Status.ALIVE;


    //==CUD 편의 메서드==
    public static Betting create(Long memberId, Long beteeId, int pointAmount){
        Betting betting = new Betting();
        betting.bettor = new ScheduleMemberValue(memberId);
        betting.betee = new ScheduleMemberValue(beteeId);
        betting.pointAmount = pointAmount;
        return betting;
    }

}

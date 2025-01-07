package common.domain;

import common.domain.value_reference.ScheduleMemberValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Racing extends BaseTime{

    @Column(name = "racingId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AttributeOverride(name = "id", column = @Column(name = "firstRacerScheduleMemberId"))
    @Embedded
    private ScheduleMemberValue firstRacer;

    @AttributeOverride(name = "id", column = @Column(name = "secondRacerScheduleMemberId"))
    @Embedded
    private ScheduleMemberValue secondRacer;

    @AttributeOverride(name = "id", column = @Column(name = "winnerScheduleMemberId"))
    @Embedded
    private ScheduleMemberValue winner;

    private int pointAmount;

    @Enumerated(value = EnumType.STRING)
    private ExecStatus raceStatus;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    public static Racing create(Long firstScheduleMemberId, Long secondScheduleMemberId, Integer pointAmount) {
        Racing racing = new Racing();
        racing.firstRacer = new ScheduleMemberValue(firstScheduleMemberId);
        racing.secondRacer = new ScheduleMemberValue(secondScheduleMemberId);
        racing.pointAmount = pointAmount;

        racing.raceStatus = ExecStatus.WAIT;
        racing.status = Status.ALIVE;

        return racing;
    }

    public void startRacing() {
        this.raceStatus = ExecStatus.RUN;
    }

    public void termRacing(Long winnerId) {
        this.raceStatus = ExecStatus.TERM;
        this.winner = new ScheduleMemberValue(winnerId);
    }

    public void specifyError() {
        this.status = Status.ERROR;
    }

}

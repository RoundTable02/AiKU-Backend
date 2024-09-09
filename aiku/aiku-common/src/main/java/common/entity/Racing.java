package common.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Racing extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bettingId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firstRacerScheduleMemberId")
    private ScheduleMember firstRacer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secondRacerScheduleMemberId")
    private ScheduleMember secondRacer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winnerScheduleMemberId")
    private ScheduleMember winner;

    private int pointAmount;

    @Enumerated(value = EnumType.STRING)
    private ExecStatus raceStatus;

    @Enumerated(value = EnumType.STRING)
    private Status status;
}

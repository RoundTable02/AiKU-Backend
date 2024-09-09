package common.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Betting extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bettingId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bettorScheduleMemberId")
    private ScheduleMember bettor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beteeScheduleMemberId")
    private ScheduleMember betee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winnerScheduleMemberId")
    private ScheduleMember winner;

    private int pointAmount;

    @Enumerated(value = EnumType.STRING)
    private Status status;
}

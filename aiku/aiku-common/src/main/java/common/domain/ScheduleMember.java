package common.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ScheduleMember extends BaseTime{

    @Column(name = "scheduleMemberId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "scheduleId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Schedule schedule;

    private boolean isOwner;

    private boolean isPaid;
    private int pointAmount;

    private LocalDateTime arrivalTime;
    private int arrivalTimeDiff;

    @Enumerated(value = EnumType.STRING)
    private Status status;
}

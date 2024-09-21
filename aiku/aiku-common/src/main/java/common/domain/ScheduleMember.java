package common.domain;

import common.domain.member.Member;
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

    protected ScheduleMember(Member member, Schedule schedule, boolean isOwner, int pointAmount) {
        this.member = member;
        this.schedule = schedule;
        this.isOwner = isOwner;
        this.isPaid = (pointAmount > 0) ? true : false;
        this.pointAmount = pointAmount;
        this.status = Status.ALIVE;
    }

    protected void setOwner() {
        this.isOwner = true;
    }

    protected void arrive(LocalDateTime arrivalTime, int arrivalTimeDiff){
        this.arrivalTime = arrivalTime;
        this.arrivalTimeDiff = arrivalTimeDiff;
    }

    protected void setStatus(Status status) {
        this.status = status;
    }
}

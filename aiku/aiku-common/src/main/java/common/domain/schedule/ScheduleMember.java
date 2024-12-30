package common.domain.schedule;

import common.domain.BaseTime;
import common.domain.Status;
import common.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static common.domain.Status.ALIVE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ScheduleMember extends BaseTime {

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

    private int pointAmount;
    private int rewardPointAmount;

    private LocalDateTime arrivalTime;
    private int arrivalTimeDiff;

    @Enumerated(value = EnumType.STRING)
    private Status status = ALIVE;

    protected ScheduleMember(Member member, Schedule schedule, boolean isOwner, int pointAmount) {
        this.member = member;
        this.schedule = schedule;
        this.isOwner = isOwner;
        this.pointAmount = pointAmount;
    }

    protected void setOwner() {
        this.isOwner = true;
    }

    protected void arrive(LocalDateTime arrivalTime, int arrivalTimeDiff){
        this.arrivalTime = arrivalTime;
        this.arrivalTimeDiff = arrivalTimeDiff;
    }

    protected void setRewardPointAmount(int rewardPointAmount) {
        this.rewardPointAmount = rewardPointAmount;
    }

    protected void setStatus(Status status) {
        this.status = status;
    }
}

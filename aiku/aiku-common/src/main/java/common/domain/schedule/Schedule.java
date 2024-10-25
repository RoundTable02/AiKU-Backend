package common.domain.schedule;

import common.domain.*;
import common.domain.member.Member;
import common.domain.value_reference.TeamValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static common.domain.ExecStatus.*;
import static common.domain.Status.ALIVE;
import static common.domain.Status.DELETE;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Schedule extends BaseTime {

    @Column(name = "scheduleId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "teamId")
    @Embedded
    private TeamValue team;

    private String scheduleName;
    private LocalDateTime scheduleTime;

    @Embedded
    private Location location;

    private LocalDateTime scheduleTermTime;
    private boolean isAutoClose = false;

    @Enumerated(value = EnumType.STRING)
    private ExecStatus scheduleStatus = WAIT;

    @Enumerated(value = EnumType.STRING)
    private Status status = ALIVE;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<ScheduleMember> scheduleMembers = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private ScheduleResult scheduleResult;

    protected Schedule(TeamValue team, String scheduleName, LocalDateTime scheduleTime, Location location) {
        this.team = team;
        this.scheduleName = scheduleName;
        this.scheduleTime = scheduleTime;
        this.location = location;
    }

    public static Schedule create(Member member, TeamValue team, String scheduleName, LocalDateTime scheduleTime, Location location, int pointAmount) {
        Schedule schedule = new Schedule(team, scheduleName, scheduleTime, location);

        schedule.addScheduleMember(member, true, pointAmount);
        return schedule;
    }

    public void update(String scheduleName, LocalDateTime scheduleTime, Location location){
        this.scheduleName = scheduleName;
        this.scheduleTime = scheduleTime;
        this.location = location;
    }

    public void delete(){
        this.status = DELETE;
    }

    public void addScheduleMember(Member member, boolean isOwner, int pointAmount) {
        ScheduleMember scheduleMember = new ScheduleMember(member, this, isOwner, pointAmount);
        this.scheduleMembers.add(scheduleMember);
    }

    public boolean removeScheduleMember(ScheduleMember scheduleMember) {
        if(scheduleMember.getSchedule().getId().equals(id)){
            scheduleMember.setStatus(DELETE);
            return true;
        }
        return false;
    }

    public boolean changeScheduleOwner(ScheduleMember nextOwner){
        if(nextOwner.getSchedule().getId().equals(id)){
            nextOwner.setOwner();
            return true;
        }
        return false;
    }

    public boolean arriveScheduleMember(ScheduleMember scheduleMember, LocalDateTime arrivalTime){
        if(scheduleMember.getSchedule().id.equals(id)){
            scheduleMember.arrive(arrivalTime, (int) Duration.between(arrivalTime, this.scheduleTime).toMinutes());
            return true;
        }
        return false;
    }

    public boolean rewardMember(ScheduleMember scheduleMember, int rewardPointAmount){
        if(scheduleMember.getSchedule().id.equals(id)) {
            scheduleMember.setRewardPointAmount(rewardPointAmount);
            return true;
        }
        return false;
    }

    public void close(LocalDateTime scheduleCloseTime){
        setTerm(scheduleCloseTime);
    }

    public void autoClose(List<ScheduleMember> notArriveScheduleMembers, LocalDateTime closeTime){
        notArriveScheduleMembers.forEach(scheduleMember -> {
            if(scheduleMember.getSchedule().getId().equals(id)){
                scheduleMember.arrive(closeTime, -30);
            }
        });

        setTerm(closeTime);
        this.isAutoClose = true;
    }

    public void setTerm(LocalDateTime scheduleTermTime){
        this.scheduleTermTime = scheduleTermTime;
        this.scheduleStatus = TERM;
    }

    public void setRun(){
        scheduleStatus = RUN;
    }

    public void setScheduleArrivalResult(String scheduleArrivalResult) {
        checkScheduleResultExist();
        scheduleResult.setScheduleArrivalResult(scheduleArrivalResult);
    }

    public void setScheduleBettingResult(String scheduleBettingResult) {
        checkScheduleResultExist();
        scheduleResult.setScheduleBettingResult(scheduleBettingResult);
    }

    public void setScheduleRacingResult(String scheduleRacingResult) {
        checkScheduleResultExist();
        scheduleResult.setScheduleRacingResult(scheduleRacingResult);
    }

    private void checkScheduleResultExist(){
        if(scheduleResult == null){
            scheduleResult = new ScheduleResult(this);
        }
    }
}

package common.domain;

import common.domain.member.Member;
import common.domain.value_reference.TeamValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Schedule extends BaseTime{

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

    @Enumerated(value = EnumType.STRING)
    private ExecStatus scheduleStatus;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<ScheduleMember> scheduleMembers = new ArrayList<>();

    protected Schedule(TeamValue team, String scheduleName, LocalDateTime scheduleTime, Location location) {
        this.team = team;
        this.scheduleName = scheduleName;
        this.scheduleTime = scheduleTime;
        this.location = location;
        this.status = Status.ALIVE;
    }

    //==CUD 편의 메서드==
    public static Schedule create(Member member, Long teamId, String scheduleName, LocalDateTime scheduleTime, Location location, int pointAmount) {
        //스케줄 생성
        TeamValue team = new TeamValue(teamId);
        Schedule schedule = new Schedule(team, scheduleName, scheduleTime, location);
        schedule.setScheduleStatus(ExecStatus.WAIT);

        //생성자를 스케줄 멤버로 추가
        schedule.addScheduleMember(member, true, pointAmount);
        return schedule;
    }

    public void update(String scheduleName, LocalDateTime scheduleTime, Location location){
        this.scheduleName = scheduleName;
        this.scheduleTime = scheduleTime;
        this.location = location;
    }

    //==편의 메서드==
    public void addScheduleMember(Member member, boolean isOwner, int pointAmount) {
        ScheduleMember scheduleMember = new ScheduleMember(member, this, isOwner, pointAmount);
        this.scheduleMembers.add(scheduleMember);
    }

    public int removeScheduleMember(Member member) {
        for (int i = 0; i < scheduleMembers.size(); i++) {
            ScheduleMember scheduleMember = scheduleMembers.get(i);
            if(scheduleMember.getMember().getId() == member.getId()){
                scheduleMember.setStatus(Status.DELETE);
                return scheduleMember.getPointAmount();
            }
        }
        return 0;
    }

    public void setScheduleStatus(ExecStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }
}

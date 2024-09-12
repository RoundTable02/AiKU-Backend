package common.domain;

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
    private List<ScheduleMember> scheduleMember = new ArrayList<>();

    protected Schedule(TeamValue team, String scheduleName, LocalDateTime scheduleTime, Location location) {
        this.team = team;
        this.scheduleName = scheduleName;
        this.scheduleTime = scheduleTime;
        this.location = location;
    }

    //==생성 편의 메서드==
    public static Schedule create(Member member, Long teamId, String scheduleName, LocalDateTime scheduleTime, Location location) {
        //스케줄 생성
        TeamValue team = new TeamValue(teamId);
        Schedule schedule = new Schedule(team, scheduleName, scheduleTime, location);

        //생성자를 스케줄 멤버로 추가
        schedule.addScheduleMember();
        return schedule;
    }

    //==편의 메서드==
    public void addScheduleMember(){
        ScheduleMember scheduleMember = new ScheduleMember();
//        this.scheduleMember.add();
    }
}

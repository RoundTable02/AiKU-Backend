package common.domain.schedule;

import common.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ScheduleResult extends BaseTime {

    @Column(name = "scheduleResultId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "scheduleId")
    @OneToOne(fetch = FetchType.LAZY)
    private Schedule schedule;

    @Lob
    private String scheduleArrivalResult;
    @Lob
    private String scheduleBettingResult;
    @Lob
    private String scheduleRacingResult;

    //==편의 메서드==
    protected void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    protected void setScheduleArrivalResult(String scheduleArrivalResult) {
        this.scheduleArrivalResult = scheduleArrivalResult;
    }

    protected void setScheduleBettingResult(String scheduleBettingResult) {
        this.scheduleBettingResult = scheduleBettingResult;
    }

    protected void setScheduleRacingResult(String scheduleRacingResult) {
        this.scheduleRacingResult = scheduleRacingResult;
    }
}

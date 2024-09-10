package common.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ScheduleResult extends BaseTime{

    @Column(name = "scheduleResultId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "scheduleId")
    @OneToOne(fetch = FetchType.LAZY)
    private Schedule schedule;

    private String scheduleArrivalResult;
    private String scheduleBettingResult;
    private String scheduleRacingResult;
}

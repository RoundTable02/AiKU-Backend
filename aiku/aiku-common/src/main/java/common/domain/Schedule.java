package common.domain;

import common.domain.value_reference.TeamValue;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
}

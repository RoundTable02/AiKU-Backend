package common.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Schedule extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheduleId")
    private Long id;

    private String scheduleName;
    private LocalDateTime scheduleTime;

    @Embedded
    private Location location;

    @Enumerated(value = EnumType.STRING)
    private ExecStatus scheduleStatus;

    @Enumerated(value = EnumType.STRING)
    private Status status;
}

package aiku_main.dto;

import common.domain.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleAddDto {
    private String scheduleName;
    private Location location;
    private LocalDateTime scheduleTime;
    private int pointAmount;
}

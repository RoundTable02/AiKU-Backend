package aiku_main.dto;

import common.domain.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleAddDto {
    public String scheduleName;
    public Location location;
    public LocalDateTime scheduleTime;
    public int pointAmount;
}

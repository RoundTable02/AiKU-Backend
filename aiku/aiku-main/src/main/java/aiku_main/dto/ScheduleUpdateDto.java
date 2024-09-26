package aiku_main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleUpdateDto {

    public String scheduleName;
    public LocationDto location;
    public LocalDateTime scheduleTime;
}

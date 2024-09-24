package aiku_main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleAddDto {
    private String scheduleName;
    private LocationDto location;
    private LocalDateTime scheduleTime;
    private int pointAmount;
}

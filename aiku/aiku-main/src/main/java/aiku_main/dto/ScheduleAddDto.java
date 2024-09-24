package aiku_main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ScheduleAddDto {

    @NotBlank @Size(max = 15)
    private String scheduleName;
    private LocationDto location;
    @NotNull
    private LocalDateTime scheduleTime;
    private int pointAmount;
}

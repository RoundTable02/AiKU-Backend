package aiku_main.dto.schedule;

import aiku_main.controller.validation.ValidScheduleTime;
import aiku_main.dto.LocationDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleAddDto {

    @NotBlank @Size(max = 15)
    private String scheduleName;
    @Valid
    private LocationDto location;
    @ValidScheduleTime
    private LocalDateTime scheduleTime;
}

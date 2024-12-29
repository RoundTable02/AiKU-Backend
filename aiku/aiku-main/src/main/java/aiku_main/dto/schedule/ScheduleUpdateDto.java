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
public class ScheduleUpdateDto {

    @NotBlank @Size(max = 15)
    public String scheduleName;
    @Valid
    public LocationDto location;
    @ValidScheduleTime
    public LocalDateTime scheduleTime;
}

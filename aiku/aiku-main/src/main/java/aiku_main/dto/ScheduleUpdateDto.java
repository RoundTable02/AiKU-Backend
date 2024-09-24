package aiku_main.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    public LocalDateTime scheduleTime;
}

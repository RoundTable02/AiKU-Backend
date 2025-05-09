package aiku_main.dto.schedule.result.racing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RacingResultDto {

    private Long scheduleId;
    private List<RacingResult> data;
}

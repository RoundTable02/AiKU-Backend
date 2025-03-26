package aiku_main.dto.racing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRacingResult {

    private Long scheduleId;
    private List<ScheduleRacing> data;
}

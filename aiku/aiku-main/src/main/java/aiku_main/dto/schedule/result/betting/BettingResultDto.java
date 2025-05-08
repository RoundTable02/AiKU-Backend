package aiku_main.dto.schedule.result.betting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BettingResultDto {

    private Long scheduleId;
    private List<BettingResult> data;
}

package aiku_main.dto.betting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleBettingResult {

    private Long scheduleId;
    private List<ScheduleBetting> data;
}

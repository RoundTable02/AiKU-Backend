package aiku_main.dto.schedule.result.arrival_time;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ScheduleArrivalResultDto {

    private Long scheduleId;
    private List<ScheduleArrivalResult> members;

    public ScheduleArrivalResultDto(Long scheduleId, List<ScheduleArrivalResult> members) {
        this.scheduleId = scheduleId;
        this.members = members;
    }
}

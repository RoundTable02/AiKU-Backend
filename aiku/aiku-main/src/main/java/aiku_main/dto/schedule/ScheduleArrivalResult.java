package aiku_main.dto.schedule;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ScheduleArrivalResult {

    private Long scheduleId;
    private List<ScheduleArrivalMember> members;

    public ScheduleArrivalResult(Long scheduleId, List<ScheduleArrivalMember> members) {
        this.scheduleId = scheduleId;
        this.members = members;
    }
}

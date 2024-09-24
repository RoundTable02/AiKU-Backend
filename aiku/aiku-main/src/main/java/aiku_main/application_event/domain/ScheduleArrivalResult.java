package aiku_main.application_event.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ScheduleArrivalResult {
    private List<ScheduleArrivalMember> data;

    public ScheduleArrivalResult(List<ScheduleArrivalMember> data) {
        this.data = data;
    }
}

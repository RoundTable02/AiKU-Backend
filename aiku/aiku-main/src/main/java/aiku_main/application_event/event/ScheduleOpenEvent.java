package aiku_main.application_event.event;

import common.domain.schedule.Schedule;
import common.domain.value_reference.ScheduleValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ScheduleOpenEvent {

    private ScheduleValue schedule;

    public ScheduleOpenEvent(Schedule schedule) {
        this.schedule = new ScheduleValue(schedule);
    }
}

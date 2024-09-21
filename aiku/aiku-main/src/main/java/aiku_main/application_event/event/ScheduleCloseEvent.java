package aiku_main.application_event.event;

import common.domain.Schedule;
import common.domain.value_reference.ScheduleValue;
import lombok.Getter;

@Getter
public class ScheduleCloseEvent {
    private ScheduleValue schedule;

    public ScheduleCloseEvent(Schedule schedule) {
        this.schedule = new ScheduleValue(schedule);
    }
}

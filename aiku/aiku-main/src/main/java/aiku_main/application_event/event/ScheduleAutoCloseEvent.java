package aiku_main.application_event.event;

import common.domain.schedule.Schedule;
import common.domain.value_reference.ScheduleValue;
import lombok.Getter;

@Getter
public class ScheduleAutoCloseEvent {

    private ScheduleValue schedule;

    public ScheduleAutoCloseEvent(Schedule schedule) {
        this.schedule = new ScheduleValue(schedule);
    }
}

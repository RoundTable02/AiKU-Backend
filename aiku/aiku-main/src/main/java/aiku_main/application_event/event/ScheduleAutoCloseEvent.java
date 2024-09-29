package aiku_main.application_event.event;

import common.domain.schedule.Schedule;
import common.domain.team.Team;
import common.domain.value_reference.ScheduleValue;
import common.domain.value_reference.TeamValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ScheduleAutoCloseEvent {

    private ScheduleValue schedule;

    public ScheduleAutoCloseEvent(Schedule schedule) {
        this.schedule = new ScheduleValue(schedule);
    }
}

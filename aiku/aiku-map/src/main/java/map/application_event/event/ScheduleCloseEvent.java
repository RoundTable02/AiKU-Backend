package map.application_event.event;

import lombok.Getter;

@Getter
public class ScheduleCloseEvent {

    private Long scheduleId;

    public ScheduleCloseEvent(Long scheduleId) {
        this.scheduleId = scheduleId;
    }
}

package aiku_main.application_event.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleCloseEvent {

    private Long teamId;
    private Long scheduleId;
}

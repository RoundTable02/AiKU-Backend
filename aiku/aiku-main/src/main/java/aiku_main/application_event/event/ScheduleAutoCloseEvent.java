package aiku_main.application_event.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ScheduleAutoCloseEvent {

    private Long scheduleId;
}

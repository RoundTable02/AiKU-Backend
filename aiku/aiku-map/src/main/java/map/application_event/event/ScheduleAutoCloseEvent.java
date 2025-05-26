package map.application_event.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ScheduleAutoCloseEvent {

    private Long scheduleId;
    private LocalDateTime closeTime;

}

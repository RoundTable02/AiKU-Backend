package aiku_main.application_event.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduleExitEvent {

    private Long memberId;
    private Long scheduleMemberId;
    private Long scheduleId;
}

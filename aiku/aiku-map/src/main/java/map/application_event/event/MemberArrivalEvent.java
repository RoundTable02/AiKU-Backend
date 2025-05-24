package map.application_event.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import map.application_event.domain.RacingInfo;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class MemberArrivalEvent {

    private Long memberId;
    private Long scheduleId;
    private String scheduleName;
    private LocalDateTime arrivalTime;
}

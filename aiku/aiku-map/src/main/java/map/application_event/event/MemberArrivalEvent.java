package map.application_event.event;

import lombok.Getter;
import map.application_event.domain.RacingInfo;

@Getter
public class MemberArrivalEvent {

    private Long memberId;
    private Long scheduleId;
    private String scheduleName;

    public MemberArrivalEvent(Long memberId, Long scheduleId, String scheduleName) {
        this.memberId = memberId;
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
    }
}

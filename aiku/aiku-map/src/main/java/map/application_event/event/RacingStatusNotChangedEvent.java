package map.application_event.event;

import lombok.Getter;
import map.application_event.domain.RacingInfo;

@Getter
public class RacingStatusNotChangedEvent {

    private RacingInfo racingInfo;

    public RacingStatusNotChangedEvent(RacingInfo racingInfo) {
        this.racingInfo = racingInfo;
    }
}

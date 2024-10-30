package map.application_event.event;

import common.domain.member.Member;
import common.domain.value_reference.MemberValue;
import lombok.Getter;
import map.application_event.domain.RacingInfo;

@Getter
public class AskRacingEvent {

    private RacingInfo racingInfo;

    public AskRacingEvent(RacingInfo racingInfo) {
        this.racingInfo = racingInfo;
    }
}

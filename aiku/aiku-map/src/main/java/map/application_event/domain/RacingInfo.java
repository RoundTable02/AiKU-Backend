package map.application_event.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RacingInfo {
    private Long scheduleId;
    private String scheduleName;
    private Long racingId;
    private Long firstRacerId;
    private Long secondRacerId;
    private int pointAmount;
}

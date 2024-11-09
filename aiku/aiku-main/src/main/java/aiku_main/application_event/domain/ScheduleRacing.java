package aiku_main.application_event.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRacing {

    private ScheduleRacingMember firstRacer;
    private ScheduleRacingMember secondRacer;
    private int pointAmount;
}

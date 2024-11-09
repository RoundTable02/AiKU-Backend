package aiku_main.application_event.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleRacing {

    private ScheduleRacingMember bettor;
    private ScheduleRacingMember betee;
    private int pointAmount;
}

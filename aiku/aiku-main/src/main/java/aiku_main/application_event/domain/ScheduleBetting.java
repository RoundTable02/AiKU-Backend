package aiku_main.application_event.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleBetting {
    private ScheduleBettingMember bettor;
    private ScheduleBettingMember betee;
    private int pointAmount;
}

package aiku_main.application_event.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamExitEvent{

    private Long memberId;
    private Long teamId;
}

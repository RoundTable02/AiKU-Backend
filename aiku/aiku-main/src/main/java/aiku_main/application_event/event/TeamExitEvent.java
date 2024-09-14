package aiku_main.application_event.event;

import common.domain.value_reference.MemberValue;
import common.domain.value_reference.TeamValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeamExitEvent{
    private MemberValue member;
    private TeamValue team;
}

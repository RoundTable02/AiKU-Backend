package aiku_main.application_event.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamLateTimeResult {
    private Long groupId;
    private List<TeamResultMember> members;
}

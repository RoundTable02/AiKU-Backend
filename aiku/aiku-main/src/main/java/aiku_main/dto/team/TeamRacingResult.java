package aiku_main.dto.team;

import aiku_main.dto.team.result.late_time.TeamLateTimeResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamRacingResult {

    private Long groupId;
    private List<TeamLateTimeResult> members;
}

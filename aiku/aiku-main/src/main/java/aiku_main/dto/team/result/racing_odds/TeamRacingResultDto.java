package aiku_main.dto.team.result.racing_odds;

import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@AllArgsConstructor
public class TeamRacingResultDto {

    private Long groupId;
    private List<TeamRacingResult> members;
}

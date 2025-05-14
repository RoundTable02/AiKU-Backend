package aiku_main.dto.team.result.racing_odds;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamRacingResultDto {

    private Long groupId;
    private List<TeamRacingResult> members;
}

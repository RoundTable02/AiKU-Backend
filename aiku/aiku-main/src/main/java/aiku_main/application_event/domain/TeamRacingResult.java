package aiku_main.application_event.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TeamRacingResult {
    private Long groupId;
    private List<TeamResultMember> members;
}

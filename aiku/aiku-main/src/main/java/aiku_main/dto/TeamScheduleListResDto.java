package aiku_main.dto;

import common.domain.team.Team;
import lombok.Getter;

import java.util.List;

@Getter
public class TeamScheduleListResDto {

    private Long totalCount;
    private int page;
    private Long groupId;
    private int runSchedule;
    private int waitSchedule;
    private List<TeamScheduleListEachResDto> data;

    public TeamScheduleListResDto(Team team, Long totalCount, int page, int runSchedule, int waitSchedule, List<TeamScheduleListEachResDto> data) {
        this.groupId = team.getId();
        this.totalCount = totalCount;
        this.page = page;
        this.runSchedule = runSchedule;
        this.waitSchedule = waitSchedule;
        this.data = data;
    }
}

package aiku_main.dto;

import common.domain.Schedule;
import lombok.Getter;

import java.util.List;

@Getter
public class ScheduleDetailResDto {
    private Long scheduleId;
    private String scheduleName;
    private LocationDto location;
    List<ScheduleMemberResDto> members;

    public ScheduleDetailResDto(Schedule schedule, List<ScheduleMemberResDto> members) {
        this.scheduleId = schedule.getId();
        this.scheduleName = schedule.getScheduleName();
        this.location = new LocationDto(schedule.getLocation());
        this.members = members;
    }
}

package aiku_main.dto.schedule;

import aiku_main.dto.LocationDto;
import common.domain.schedule.Schedule;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ScheduleDetailResDto {

    private Long scheduleId;
    private String scheduleName;
    private LocalDateTime scheduleTime;
    private LocationDto location;
    List<ScheduleMemberResDto> members;

    public ScheduleDetailResDto(Schedule schedule, List<ScheduleMemberResDto> members) {
        this.scheduleId = schedule.getId();
        this.scheduleName = schedule.getScheduleName();
        this.scheduleTime = schedule.getScheduleTime();
        this.location = new LocationDto(schedule.getLocation());
        this.members = members;
    }
}

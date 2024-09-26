package aiku_main.dto;

import com.querydsl.core.annotations.QueryProjection;
import common.domain.ExecStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberScheduleListEachResDto {

    private Long groupId;
    private String groupName;
    private Long scheduleId;
    private String scheduleName;
    private LocationDto location;
    private LocalDateTime scheduleTime;
    private int memberSize;
    private ExecStatus scheduleStatus;

    @QueryProjection
    public MemberScheduleListEachResDto(Long groupId, String groupName, Long scheduleId, String scheduleName,
                                        LocationDto location, LocalDateTime scheduleTime,
                                      ExecStatus scheduleStatus, int memberCount) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.location = location;
        this.scheduleTime = scheduleTime;
        this.scheduleStatus = scheduleStatus;
        this.memberSize = memberCount;
    }
}

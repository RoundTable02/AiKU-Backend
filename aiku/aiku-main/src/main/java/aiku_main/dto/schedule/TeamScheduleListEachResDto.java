package aiku_main.dto.schedule;

import aiku_main.dto.LocationDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import common.domain.ExecStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
public class TeamScheduleListEachResDto {

    private Long scheduleId;
    private String scheduleName;
    private LocationDto location;
    private LocalDateTime scheduleTime;
    private ExecStatus scheduleStatus;
    private int memberSize;
    private boolean accept = false;

    @JsonIgnore
    List<Long> membersIdList;

    @QueryProjection
    public TeamScheduleListEachResDto(Long scheduleId, String scheduleName, LocationDto location, LocalDateTime scheduleTime,
                                      ExecStatus scheduleStatus, String membersIdStringList) {
        this.scheduleId = scheduleId;
        this.scheduleName = scheduleName;
        this.location = location;
        this.scheduleTime = scheduleTime;
        this.scheduleStatus = scheduleStatus;

        membersIdList = Arrays.stream(membersIdStringList.split(","))
                .map(Long::parseLong)
                .toList();
        this.memberSize = membersIdList.size();
    }

    public void setAccept(Long memberId){
        for (Long acceptId : membersIdList) {
            if(memberId.equals(acceptId)){
                this.accept = true;
            }
        }
    }
}

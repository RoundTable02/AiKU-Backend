package aiku_main.repository;

import aiku_main.dto.ScheduleMemberResDto;

import java.util.List;

public interface ScheduleReadRepository{
    List<ScheduleMemberResDto> getScheduleMembersWithMember(Long scheduleId);
}

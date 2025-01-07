package map.repository;

import common.domain.schedule.ScheduleMember;
import common.kafka_message.alarm.AlarmMemberInfo;
import map.dto.ScheduleMemberResDto;

import java.util.List;
import java.util.Optional;

public interface ScheduleQueryRepository {

    List<AlarmMemberInfo> findScheduleMemberInfosByScheduleId(Long scheduleId);

    boolean existMemberInSchedule(Long memberId, Long scheduleId);

    Optional<ScheduleMember> findScheduleMember(Long memberId, Long scheduleId);

    Optional<Long> findScheduleMemberIdByMemberAndScheduleId(Long memberId, Long scheduleId);

    List<ScheduleMemberResDto> getScheduleMembersInfo(Long scheduleId);

    List<String> findAllFcmTokensInSchedule(Long scheduleId);
}

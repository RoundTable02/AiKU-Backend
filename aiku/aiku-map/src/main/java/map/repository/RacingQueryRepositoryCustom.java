package map.repository;

import common.domain.Racing;
import common.kafka_message.alarm.AlarmMemberInfo;
import map.dto.RacingResDto;
import map.dto.RunningRacingDto;

import java.util.List;

public interface RacingQueryRepositoryCustom {

    List<RacingResDto> getAllRunningRacingsInSchedule(Long scheduleId);

    boolean checkBothMemberHaveEnoughRacingPoint(Long racingId);

    List<AlarmMemberInfo> findMemberInfoByScheduleMemberId(Long racingId);

    boolean checkMemberIsSecondRacerInRacing(Long memberId, Long racingId);

    boolean existsByFirstMemberIdAndSecondMemberId(Long scheduleId, Long firstMemberId, Long secondMemberId);

    List<Racing> findRacingsByMemberId(Long memberId);

    List<RunningRacingDto> findRunningRacingsByScheduleMemberId(Long scheduleMemberId);
}

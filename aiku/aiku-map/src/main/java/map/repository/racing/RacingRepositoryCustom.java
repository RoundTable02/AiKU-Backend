package map.repository.racing;

import common.kafka_message.alarm.AlarmMemberInfo;
import map.dto.RacingResDto;
import map.dto.RunningRacingDto;
import map.dto.TermRacingDto;

import java.util.List;

public interface RacingRepositoryCustom {

    void setWinnerAndTermRacingByScheduleMemberId(Long scheduleMemberId);

    void terminateRunningRacing(Long scheduleId);

    void cancelRacing(Long racingId);

    List<RacingResDto> getAllRunningRacingsInSchedule(Long scheduleId);

    boolean checkBothMemberHaveEnoughRacingPoint(Long racingId);

    List<AlarmMemberInfo> findMemberInfoByScheduleMemberId(Long racingId);

    boolean checkMemberIsSecondRacerInRacing(Long memberId, Long racingId);

    boolean existsByFirstMemberIdAndSecondMemberId(Long scheduleId, Long firstMemberId, Long secondMemberId);

    List<RunningRacingDto> findRunningRacingsByScheduleMemberId(Long scheduleMemberId);

    List<TermRacingDto> findTermRacingIdsWithNoWinnerInSchedule(Long scheduleId);

    List<String> findRacersFcmTokensInRacing(Long racingId);
}

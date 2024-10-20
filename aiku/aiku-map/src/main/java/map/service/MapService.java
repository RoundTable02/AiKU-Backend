package map.service;

import common.domain.Status;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import common.kafka_message.KafkaTopic;
import common.kafka_message.alarm.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import map.dto.*;
import map.exception.ScheduleException;
import map.kafka.KafkaProducerService;
import map.repository.MemberRepository;
import map.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static common.domain.ExecStatus.RUN;
import static common.response.status.BaseErrorCode.NOT_IN_SCHEDULE;
import static common.response.status.BaseErrorCode.NO_SUCH_SCHEDULE;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MapService {

    private final KafkaProducerService kafkaService;
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;

    public Long sendLocation(Long memberId, Long scheduleId, RealTimeLocationDto realTimeLocationDto) {
        // 카프카로 위도, 경도 데이터를 스케줄 상의 다른 유저에게 전송하는 로직
        List<AlarmMemberInfo> alarmMemberInfos = getScheduleMemberInfos(scheduleId);

        kafkaService.sendMessage(KafkaTopic.alarm,
                new LocationAlarmMessage(alarmMemberInfos, AlarmMessageType.MEMBER_REAL_TIME_LOCATION,
                        memberId,
                        scheduleId,
                        realTimeLocationDto.getLatitude(),
                        realTimeLocationDto.getLongitude()
                )
        );

        return scheduleId;
    }


    @Transactional
    public Long makeMemberArrive(Long memberId, Long scheduleId, MemberArrivalDto arrivalDto) {
        // 해당 멤버 스케줄에 존재 / 스케줄이 진행 중인지 검증,
        checkMemberInSchedule(memberId, scheduleId);
        checkScheduleInRun(scheduleId);

        //  ScheduleMember에 해당 멤버 도착 저장
        ScheduleMember scheduleMember = findScheduleMember(memberId, scheduleId);
        Schedule schedule = findSchedule(scheduleId);
        schedule.arriveScheduleMember(scheduleMember, arrivalDto.getArrivalTime());

        //  해당 약속의 멤버들에게 멤버 도착 카프카로 전달
        List<AlarmMemberInfo> alarmMemberInfos = getScheduleMemberInfos(scheduleId);

        kafkaService.sendMessage(KafkaTopic.alarm,
                new ArrivalAlarmMessage(alarmMemberInfos, AlarmMessageType.MEMBER_ARRIVAL,
                        memberId,
                        scheduleId,
                        schedule.getScheduleName(),
                        arrivalDto.getArrivalTime()
                )
        );

        //  모든 멤버 도착 확인, 카프카로 스케줄 종료 전달
        if(schedule.checkAllMembersArrive()) {
            kafkaService.sendMessage(KafkaTopic.alarm,
                    new ScheduleClosedMessage(alarmMemberInfos, AlarmMessageType.SCHEDULE_MAP_CLOSE,
                            scheduleId,
                            schedule.getScheduleName(),
                            schedule.getLocation().getLocationName(),
                            schedule.getScheduleTime()
                    )
            );
        }

        return scheduleId;
    }

    public Long sendEmoji(Long memberId, Long scheduleId, EmojiDto emojiDto) {
        // TODO : 해당 멤버 스케줄에 존재 / 스케줄이 진행 중인지 검증,
        //  카프카로 이모지 데이터 전송
        //  @return scheduleId
        return null;
    }

    public DataResDto<List<RacingResDto>> getRacings(Long memberId, Long scheduleId) {
        // TODO : 해당 멤버 스케줄에 존재 / 스케줄이 진행 중인지 검증,
        //  해당 스케줄에 속해 있으며 현재 진행 중인 레이싱 종합하여 전달
        return null;
    }

    @Transactional
    public Long makeRacing(Long memberId, Long scheduleId, RacingAddDto racingAddDto) {
        // TODO : 해당 멤버 스케줄에 존재 / 스케줄이 진행 중인지 검증,
        //  두 유저 모두 깍두기 멤버가 아닌지, 이미 중복된 레이싱이 존재하는지 검증,
        //  두 유저 모두 충분한 포인트를 가졌는지 확인 (누가 돈이 없는지 에러 분리)
        //  대기 중 레이싱 DB 저장,
        //  카프카로 레이싱 신청 대상자에게 알림 전달
        //  Runnable 이용해 30초 후 레이싱 상태 확인, 대기 중이면 삭제하고 두 사용자에게 알림 발송하는 로직 실행
        //  @return racingId
        return null;
    }

    @Transactional
    public Long acceptRacing(Long memberId, Long scheduleId, Long racingId) {
        // TODO : 레이싱이 대기 중인지 검증,
        //  두 유저 모두 충분한 포인트를 가졌는지 확인 (누가 돈이 없는지 에러 분리)
        //  카프카로 레이싱 신청자에게 알림 전달 두 대상자의 포인트 차감, 레이싱 상태 진행 중으로 변경
        //  @return racingId
        return null;
    }


    @Transactional
    public Long denyRacing(Long memberId, Long scheduleId, Long racingId) {
        // TODO : 레이싱이 대기 중인지 검증, 거절을 누른 멤버가 레이싱의 대상자인지 검증
        //  대기 중 레이싱 DB 삭제,
        //  카프카로 레이싱 신청 참가자에게 알림 전송
        //  @return racingId
        return null;
    }

    private Schedule findSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(NO_SUCH_SCHEDULE));
    }

    private ScheduleMember findScheduleMember(Long memberId, Long scheduleId) {
        return scheduleRepository.findScheduleMember(memberId, scheduleId)
                .orElseThrow(() -> new ScheduleException(NO_SUCH_SCHEDULE));
    }

    private List<AlarmMemberInfo> getScheduleMemberInfos(Long scheduleId) {
        return scheduleRepository.findScheduleMemberInfosByScheduleId(scheduleId);
    }

    private void checkMemberInSchedule(Long memberId, Long scheduleId) {
        if(!scheduleRepository.existMemberInSchedule(memberId, scheduleId)) {
            throw new ScheduleException(NOT_IN_SCHEDULE);
        }
    }

    private void checkScheduleInRun(Long scheduleId) {
        if (scheduleRepository.existsByIdAndScheduleStatusAndStatus(scheduleId, RUN, Status.ALIVE)) {
            throw new ScheduleException(NO_SUCH_SCHEDULE);
        }
    }
}

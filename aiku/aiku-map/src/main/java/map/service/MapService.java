package map.service;

import common.domain.Racing;
import common.domain.Status;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import common.exception.PaidMemberLimitException;
import common.kafka_message.KafkaTopic;
import common.kafka_message.alarm.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import map.application_event.publisher.RacingEventPublisher;
import map.dto.*;
import map.exception.MemberNotFoundException;
import map.exception.ScheduleException;
import map.kafka.KafkaProducerService;
import map.repository.MemberRepository;
import map.repository.RacingRepository;
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
        // 해당 멤버 스케줄에 존재 / 스케줄이 진행 중인지 검증,
        checkMemberInSchedule(memberId, scheduleId);
        checkScheduleInRun(scheduleId);

        //  카프카로 이모지 데이터 전송
        AlarmMemberInfo sender = getMemberInfo(memberId);
        AlarmMemberInfo receiver = getMemberInfo(emojiDto.getReceiverId());

        List<AlarmMemberInfo> alarmMemberInfos = List.of(sender, receiver);

        Schedule schedule = findSchedule(scheduleId);

        kafkaService.sendMessage(KafkaTopic.alarm,
                new EmojiMessage(alarmMemberInfos, AlarmMessageType.EMOJI,
                        scheduleId,
                        schedule.getScheduleName(),
                        emojiDto.getEmojiType().name()
                )
        );

        return scheduleId;
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

    private AlarmMemberInfo getMemberInfo(Long memberId) {
        return memberRepository.findMemberInfo(memberId)
                .orElseThrow(() -> new MemberNotFoundException());
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

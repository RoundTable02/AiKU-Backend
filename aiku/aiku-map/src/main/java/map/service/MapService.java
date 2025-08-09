package map.service;

import common.domain.Arrival;
import common.domain.Location;
import common.domain.Status;
import common.domain.schedule.Schedule;
import common.domain.schedule.ScheduleMember;
import common.kafka_message.KafkaTopic;
import common.kafka_message.ScheduleCloseMessage;
import common.kafka_message.alarm.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import map.application_event.event.MemberArrivalEvent;
import map.application_event.event.ScheduleCloseEvent;
import map.dto.*;
import map.exception.MemberNotFoundException;
import map.exception.ScheduleException;
import map.kafka.KafkaProducerService;
import map.repository.arrival.ArrivalRepository;
import map.repository.member.MemberRepository;
import map.repository.schedule.ScheduleRepository;
import map.repository.schedule_location.ScheduleLocationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final ScheduleLocationRepository scheduleLocationRepository;
    private final ArrivalRepository arrivalRepository;

    private final ApplicationEventPublisher publisher;

    public ScheduleDetailResDto getScheduleDetail(Long memberId, Long scheduleId) {
        Schedule schedule = findSchedule(scheduleId);
        checkMemberInSchedule(memberId, scheduleId);

        List<ScheduleMemberResDto> scheduleMembers = scheduleRepository.getScheduleMembersInfo(scheduleId);
        return new ScheduleDetailResDto(schedule, scheduleMembers);
    }

    @Transactional
    public LocationsResponseDto saveAndSendAllLocation(Long memberId, Long scheduleId, RealTimeLocationDto realTimeLocationDto) {
        // 검증
        checkMemberInSchedule(memberId, scheduleId);

        // Redis에 해당 위치 저장
        scheduleLocationRepository.saveLocation(scheduleId, memberId, realTimeLocationDto.getLatitude(), realTimeLocationDto.getLongitude());

        // Redis에 담긴 scheduleId에 해당하는 모든 위치 Load
        List<RealTimeLocationResDto> scheduleLocations = scheduleLocationRepository.getScheduleLocations(scheduleId);

        // Response로 전달
        return new LocationsResponseDto(scheduleLocations.size(), scheduleLocations);
    }

    // 도착한 사람은 위치 정보 보낼 필요 없이 GET 할 수 있도록
    public LocationsResponseDto getAllLocation(Long memberId, Long scheduleId) {
        // 검증
        checkMemberInSchedule(memberId, scheduleId);

        // Redis에 담긴 scheduleId에 해당하는 모든 위치 Load
        List<RealTimeLocationResDto> scheduleLocations = scheduleLocationRepository.getScheduleLocations(scheduleId);

        // Response로 전달
        return new LocationsResponseDto(scheduleLocations.size(), scheduleLocations);
    }

    @Transactional
    public Long makeMemberArrive(Long memberId, Long scheduleId, MemberArrivalDto arrivalDto) {
        // 해당 멤버 스케줄에 존재 / 스케줄이 진행 중인지 검증,
        checkMemberInSchedule(memberId, scheduleId);
        checkScheduleInRun(scheduleId);

        //  Arrival에 해당 멤버 도착 저장
        Schedule schedule = findSchedule(scheduleId);
        ScheduleMember scheduleMember = findScheduleMember(memberId, scheduleId);

        arrivalRepository.save(
                Arrival.builder()
                        .scheduleMemberId(scheduleMember.getId())
                        .arrivalTime(arrivalDto.getArrivalTime())
                        .build()
        );

        // 도착하면? TTL 삭제 후 도착 상태로 저장
        Location location = schedule.getLocation();
        scheduleLocationRepository.saveLocation(scheduleId, memberId, location.getLatitude(), location.getLongitude());
        scheduleLocationRepository.updateArrivalStatus(scheduleId, memberId, true);

        // 멤버 도착 이벤트 발행
        MemberArrivalEvent event = new MemberArrivalEvent(memberId, scheduleId, schedule.getScheduleName(), arrivalDto.getArrivalTime());
        publisher.publishEvent(event);

        // 모든 멤버 도착 확인, 카프카로 스케줄 종료 전달
        if(arrivalRepository.isAllMembersInScheduleArrived(scheduleId)) {
            publisher.publishEvent(new ScheduleCloseEvent(scheduleId, arrivalDto.getArrivalTime()));
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

        String receiverFcmToken = findFcmTokenByMemberId(emojiDto.getReceiverId());

        Schedule schedule = findSchedule(scheduleId);

        kafkaService.sendMessage(KafkaTopic.ALARM,
                new EmojiMessage(List.of(receiverFcmToken), AlarmMessageType.EMOJI,
                        scheduleId,
                        schedule.getScheduleName(),
                        emojiDto.getEmojiType().name(),
                        sender,
                        receiver
                )
        );

        return scheduleId;
    }

    // EventHandle : 약속 종료되면 전체 삭제
    @Transactional
    public void deleteAllLocationsInSchedule(Long scheduleId) {
        scheduleLocationRepository.deleteScheduleLocations(scheduleId);
    }

    @Transactional
    public void makeNotArrivedMemberArrive(Long scheduleId, LocalDateTime closeTime) {
        // Arrival에 저장되지 않은 ScheduleMember 조회
        List<ScheduleMember> scheduleMembers = scheduleRepository.findScheduleMembersNotInArrivalByScheduleId(scheduleId);

        // Arrival에 ScheduleMember 저장
        List<Arrival> arrivals = scheduleMembers.stream()
                .map(sm ->
                        Arrival.builder()
                                .scheduleMemberId(sm.getId())
                                .arrivalTime(closeTime)
                                .build()
                )
                .toList();

        arrivalRepository.saveAll(arrivals);
    }

    public void sendKafkaAlarmIfMemberArrived(Long memberId, Long scheduleId, String scheduleName, LocalDateTime arrivalTime) {
        List<String> fcmTokens = findAllFcmTokensInSchedule(scheduleId);
        AlarmMemberInfo arriveMemberInfo = getMemberInfo(memberId);

        // 멤버 도착 알람 전달
        kafkaService.sendMessage(KafkaTopic.ALARM,
                new ArrivalAlarmMessage(fcmTokens, AlarmMessageType.MEMBER_ARRIVAL,
                        memberId,
                        scheduleId,
                        scheduleName,
                        arrivalTime,
                        arriveMemberInfo
                )
        );
    }

    public void sendKafkaEventIfScheduleClosed(Long scheduleId, LocalDateTime closeTime) {
        kafkaService.sendMessage(KafkaTopic.SCHEDULE_CLOSE,
                new ScheduleCloseMessage(scheduleId, closeTime)
        );
    }

    private Schedule findSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(NO_SUCH_SCHEDULE));
    }

    private ScheduleMember findScheduleMember(Long memberId, Long scheduleId) {
        return scheduleRepository.findScheduleMember(memberId, scheduleId)
                .orElseThrow(() -> new ScheduleException(NO_SUCH_SCHEDULE));
    }

    private List<String> findAllFcmTokensInSchedule(Long scheduleId) {
        return scheduleRepository.findAllFcmTokensInSchedule(scheduleId);
    }

    private String findFcmTokenByMemberId(Long memberId) {
        return memberRepository.findMemberFirebaseTokenById(memberId)
                .orElseThrow(() -> new MemberNotFoundException());
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
        if (!scheduleRepository.existsByIdAndScheduleStatusAndStatus(scheduleId, RUN, Status.ALIVE)) {
            throw new ScheduleException(NO_SUCH_SCHEDULE);
        }
    }
}

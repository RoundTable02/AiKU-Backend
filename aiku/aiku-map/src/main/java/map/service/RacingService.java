package map.service;

import common.domain.Racing;
import common.domain.Status;
import common.domain.schedule.Schedule;
import common.exception.PaidMemberLimitException;
import common.kafka_message.KafkaTopic;
import common.kafka_message.PointChangeReason;
import common.kafka_message.PointChangedMessage;
import common.kafka_message.PointChangedType;
import common.kafka_message.alarm.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import map.application_event.domain.RacingInfo;
import map.application_event.publisher.RacingEventPublisher;
import map.dto.*;
import map.exception.*;
import map.kafka.KafkaProducerService;
import map.repository.MemberRepository;
import map.repository.RacingCommandRepository;
import map.repository.RacingQueryRepository;
import map.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static common.domain.ExecStatus.RUN;
import static common.domain.ExecStatus.WAIT;
import static common.response.status.BaseErrorCode.*;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RacingService {

    private final KafkaProducerService kafkaService;
    private final RacingEventPublisher racingEventPublisher;
    private final RacingQueryRepository racingQueryRepository;
    private final RacingCommandRepository racingCommandRepository;
    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;

    public DataResDto<List<RacingResDto>> getRacings(Long memberId, Long scheduleId) {
        // 해당 멤버 스케줄에 존재 / 스케줄이 진행 중인지 검증,
        checkMemberInSchedule(memberId, scheduleId);
        checkScheduleInRun(scheduleId);

        //  해당 스케줄에 속해 있으며 현재 진행 중인 레이싱 종합하여 전달
        List<RacingResDto> racings = racingQueryRepository.getAllRunningRacingsInSchedule(scheduleId);

        return new DataResDto<>(1, racings);
    }

    @Transactional
    public Long makeRacing(Long memberId, Long scheduleId, RacingAddDto racingAddDto) {
        // 해당 멤버 스케줄에 존재 / 스케줄이 진행 중인지 검증
        Long firstScheduleMemberId = getScheduleMemberIdByMemberAndScheduleId(memberId, scheduleId);
        Long secondScheduleMemberId = getScheduleMemberIdByMemberAndScheduleId(racingAddDto.getTargetMemberId(), scheduleId);
        checkScheduleInRun(scheduleId);

        //  이미 중복된 레이싱이 존재하는지 검증,
        checkDuplicateRacing(scheduleId, memberId, racingAddDto.getTargetMemberId());

        //  두 유저 모두 충분한 포인트를 가졌는지 확인
        checkEnoughRacingPoint(memberId, racingAddDto.getPoint());
        checkEnoughRacingPoint(racingAddDto.getTargetMemberId(), racingAddDto.getPoint());

        //  대기 중 레이싱 DB 저장
        Racing racing = Racing.create(firstScheduleMemberId, secondScheduleMemberId, racingAddDto.getPoint());
        racingCommandRepository.save(racing);

        //  카프카로 레이싱 신청 대상자에게 알림 전달
        AlarmMemberInfo firstRacerInfo = getMemberInfo(memberId);
        String fcmToken = findFcmTokenByMemberId(racingAddDto.getTargetMemberId());
        Schedule schedule = findSchedule(scheduleId);

        kafkaService.sendMessage(KafkaTopic.alarm,
                new AskRacingMessage(List.of(fcmToken), AlarmMessageType.ASK_RACING,
                        scheduleId,
                        schedule.getScheduleName(),
                        racing.getId(),
                        racingAddDto.getPoint(),
                        firstRacerInfo
                )
        );

        //  30초 후 레이싱 상태 확인, 대기 중이면 삭제하고 두 사용자에게 알림 발송하는 로직 실행
        racingEventPublisher.publishAskRacingEvent(
                new RacingInfo(scheduleId,
                        schedule.getScheduleName(),
                        racing.getId(),
                        memberId,
                        racingAddDto.getTargetMemberId(),
                        racingAddDto.getPoint()
                )
        );

        return racing.getId();
    }

    @Transactional
    public void autoDeleteRacingById(RacingInfo racingInfo) {
        racingCommandRepository.deleteById(racingInfo.getRacingId());
        AlarmMemberInfo secondRacerInfo = getMemberInfo(racingInfo.getSecondRacerId());
        String firstRacerFcmToken = findFcmTokenByMemberId(racingInfo.getFirstRacerId());
        String secondRacerFcmToken = findFcmTokenByMemberId(racingInfo.getSecondRacerId());

        kafkaService.sendMessage(KafkaTopic.alarm,
                new RacingAutoDeletedMessage(List.of(firstRacerFcmToken, secondRacerFcmToken),
                        AlarmMessageType.RACING_AUTO_DELETED,
                        racingInfo.getScheduleId(),
                        racingInfo.getScheduleName(),
                        racingInfo.getRacingId(),
                        racingInfo.getPointAmount(),
                        secondRacerInfo
                )
        );
    }

    @Transactional
    public Long acceptRacing(Long memberId, Long scheduleId, Long racingId) {
        // 레이싱이 대기 중인지 검증,
        checkRacingInWait(racingId);

        //  두 유저 모두 충분한 포인트를 가졌는지 확인
        checkBothMemberHaveEnoughRacingPoint(racingId);

        //  카프카로 레이싱 신청자에게 알림 전달, 레이싱 상태 진행 중으로 변경
        Racing racing = findRacing(racingId);
        racing.startRacing();

        List<AlarmMemberInfo> racingMemberInfos = getMemberInfosInRacing(racingId);
        List<String> racersTokens = findRacersFcmTokensInRacing(racingId);

        Schedule schedule = findSchedule(scheduleId);

        kafkaService.sendMessage(KafkaTopic.alarm,
                new RacingStartMessage(racersTokens,
                        AlarmMessageType.RACING_START,
                        scheduleId,
                        schedule.getScheduleName(),
                        racingId,
                        racing.getPointAmount(),
                        racingMemberInfos.get(0),
                        racingMemberInfos.get(1)
                        )
                );

        // 두 대상자에게 포인트 차감 전달
        racingMemberInfos.forEach(r ->
                kafkaService.sendMessage(KafkaTopic.alarm,
                        new PointChangedMessage(
                                r.getMemberId(),
                                PointChangedType.MINUS,
                                racing.getPointAmount(),
                                PointChangeReason.RACING,
                                racingId
                        )
                )
        );

        return racingId;
    }


    @Transactional
    public Long denyRacing(Long memberId, Long scheduleId, Long racingId) {
        // 레이싱이 대기 중인지 검증, 거절을 누른 멤버가 레이싱의 대상자인지 검증
        checkRacingInWait(racingId);
        checkMemberIsSecondRacerInRacing(memberId, racingId);

        //  카프카로 레이싱 신청 참가자에게 알림 전송
        Schedule schedule = findSchedule(scheduleId);

        List<String> racersTokens = findRacersFcmTokensInRacing(racingId);
        AlarmMemberInfo memberInfo = getMemberInfo(memberId);

        kafkaService.sendMessage(KafkaTopic.alarm,
                new RacingDeniedMessage(racersTokens,
                        AlarmMessageType.RACING_DENIED,
                        scheduleId,
                        schedule.getScheduleName(),
                        racingId,
                        memberInfo
                        )
        );

        //  대기 중 레이싱 싱테 DELETE로 변경
        racingCommandRepository.cancelRacing(racingId);

        return racingId;
    }

    // 이벤트 핸들
    @Transactional
    public void makeMemberWinnerInRacing(Long memberId, Long scheduleId, String scheduleName) {
        // 멤버의 도착으로 해당 멤버의 모든 레이싱 종료 처리
        Long scheduleMemberId = getScheduleMemberIdByMemberAndScheduleId(memberId, scheduleId);

        // 회원이 소속된 진행 중인 레이싱 정보 조회
        List<RunningRacingDto> runningRacingDtos = racingQueryRepository.findRunningRacingsByScheduleMemberId(scheduleMemberId);

        // 레이싱 종료 처리를 위한 update 벌크 쿼리
        racingCommandRepository.setWinnerAndTermRacingByScheduleMemberId(scheduleMemberId);

        runningRacingDtos.forEach(r -> {
            Long loserId = r.getFirstScheduleMemberId();
            if (r.getFirstScheduleMemberId().equals(scheduleMemberId)) {
                loserId = r.getSecondScheduleMemberId();
            }

            makeRacingTermAndWinnerPrized(scheduleId, scheduleName, r.getRacingId(), scheduleMemberId, loserId, r.getPointAmount());
        });

    }

    private void makeRacingTermAndWinnerPrized(Long scheduleId, String scheduleName, Long racingId, Long winnerScheduleMemberId, Long loserScheduleMemberId, Integer pointAmount) {
        AlarmMemberInfo winnerInfo = getMemberInfoByScheduleMemberId(winnerScheduleMemberId);
        AlarmMemberInfo loserInfo = getMemberInfoByScheduleMemberId(loserScheduleMemberId);

        // 승리자 아쿠 추가 (레이싱 성사 때 차감된 금액 + 상금)
        kafkaService.sendMessage(KafkaTopic.alarm,
                new PointChangedMessage(
                        winnerInfo.getMemberId(),
                        PointChangedType.PLUS,
                        pointAmount * 2,
                        PointChangeReason.RACING_REWARD,
                        racingId
                )
        );

        List<String> racersTokens = findRacersFcmTokensInRacing(racingId);

        // 레이싱 종료 알림
        kafkaService.sendMessage(KafkaTopic.alarm,
                new RacingTermMessage(racersTokens,
                        AlarmMessageType.RACING_TERM,
                        scheduleId,
                        scheduleName,
                        racingId,
                        pointAmount,
                        winnerInfo,
                        loserInfo
                )
        );
    }

    @Transactional
    public void terminateRunningRacing(Long scheduleId) {
        // 스케줄 종료 이후 진행 중인 레이싱 모두 무승부 처리
        racingCommandRepository.terminateRunningRacing(scheduleId);

        List<TermRacingDto> racingDtos = racingQueryRepository.findTermRacingIdsWithNoWinnerInSchedule(scheduleId);

        racingDtos.forEach(r -> {
            AlarmMemberInfo firstInfo = getMemberInfoByScheduleMemberId(r.getFirstScheduleMemberId());
            AlarmMemberInfo secondInfo = getMemberInfoByScheduleMemberId(r.getSecondScheduleMemberId());

            kafkaService.sendMessage(KafkaTopic.alarm,
                    new PointChangedMessage(
                            firstInfo.getMemberId(),
                            PointChangedType.PLUS,
                            r.getPointAmount(),
                            PointChangeReason.RACING_DRAW,
                            r.getRacingId()
                    )
            );

            kafkaService.sendMessage(KafkaTopic.alarm,
                    new PointChangedMessage(
                            secondInfo.getMemberId(),
                            PointChangedType.PLUS,
                            r.getPointAmount(),
                            PointChangeReason.RACING_DRAW,
                            r.getRacingId())
            );
        });
    }

    private Schedule findSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(NO_SUCH_SCHEDULE));
    }

    private String findFcmTokenByMemberId(Long memberId) {
        return memberRepository.findMemberFirebaseTokenById(memberId)
                .orElseThrow(() -> new MemberNotFoundException());
    }

    private AlarmMemberInfo getMemberInfo(Long memberId) {
        return memberRepository.findMemberInfo(memberId)
                .orElseThrow(() -> new MemberNotFoundException());
    }

    private AlarmMemberInfo getMemberInfoByScheduleMemberId(Long scheduleMemberId) {
        return memberRepository.findMemberInfoByScheduleMemberId(scheduleMemberId)
                .orElseThrow(() -> new MemberNotFoundException());
    }

    private Long getScheduleMemberIdByMemberAndScheduleId(Long memberId, Long scheduleId) {
        return scheduleRepository.findScheduleMemberIdByMemberAndScheduleId(memberId, scheduleId)
                .orElseThrow(() -> new ScheduleException(NOT_IN_SCHEDULE));
    }

    private Racing findRacing(Long racingId) {
        return racingQueryRepository.findById(racingId)
                .orElseThrow(() -> new RacingException(NO_SUCH_RACING));
    }

    private List<AlarmMemberInfo> getMemberInfosInRacing(Long racingId) {
        return racingQueryRepository.findMemberInfoByScheduleMemberId(racingId);
    }

    private List<String> findRacersFcmTokensInRacing(Long racingId) {
        return racingQueryRepository.findRacersFcmTokensInRacing(racingId);
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

    private void checkDuplicateRacing(Long scheduleId, Long firstMemberId, Long secondMemberId) {
        if (racingQueryRepository.existsByFirstMemberIdAndSecondMemberId(scheduleId, firstMemberId, secondMemberId)) {
            throw new RacingException(DUPLICATE_RACING);
        }
    }

    private void checkRacingInWait(Long racingId) {
        if (!racingQueryRepository.existsByIdAndRaceStatusAndStatus(racingId, WAIT, Status.ALIVE)) {
            throw new ScheduleException(NO_SUCH_SCHEDULE);
        }
    }

    private void checkEnoughRacingPoint(Long memberId, Integer point){
        if(!memberRepository.checkEnoughRacingPoint(memberId, point)){
            throw new NotEnoughPointException();
        }
    }

    private void checkBothMemberHaveEnoughRacingPoint(Long racingId){
        if(!racingQueryRepository.checkBothMemberHaveEnoughRacingPoint(racingId)){
            throw new NotEnoughPointException();
        }
    }

    private void checkMemberIsSecondRacerInRacing(Long memberId, Long racingId) {
        if (!racingQueryRepository.checkMemberIsSecondRacerInRacing(memberId, racingId)) {
            throw new RacingException(NOT_IN_RACING);
        }
    }
}

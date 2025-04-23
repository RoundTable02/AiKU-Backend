package aiku_main.service;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.exception.MemberNotFoundException;
import aiku_main.kafka.KafkaProducerService;
import aiku_main.repository.member.MemberRepository;
import aiku_main.service.strategy.RollbackProcessor;
import common.kafka_message.KafkaTopic;
import common.kafka_message.alarm.AlarmMessageType;
import common.kafka_message.alarm.PointErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PointChangeFailSagaService {

    private final RollbackProcessor rollbackProcessor;
    private final MemberRepository memberRepository;
    private final KafkaProducerService kafkaProducerService;

    public void notifyAndRollbackPointChange(Long memberId, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        // 각 전략의 로직 실행
        rollbackProcessor.process(memberId, pointChangeType, pointAmount, pointChangeReason, reasonId);

        // 알림 발송
        String firebaseToken = memberRepository.findFirebaseTokenByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException());

        kafkaProducerService.sendMessage(KafkaTopic.alarm,
                new PointErrorMessage(List.of(firebaseToken), AlarmMessageType.POINT_ERROR)
        );
    }
}

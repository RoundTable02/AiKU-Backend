package aiku_main.service;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.kafka.KafkaProducerService;
import aiku_main.service.strategy.RollbackProcessor;
import common.domain.value_reference.MemberValue;
import common.kafka_message.KafkaTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointChangeFailSagaService {

    private final RollbackProcessor rollbackProcessor;

    private final KafkaProducerService kafkaProducerService;

    public void notifyAndRollbackPointChange(MemberValue member, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        // 각 전략의 로직 실행
        rollbackProcessor.process(member, pointChangeType, pointAmount, pointChangeReason, reasonId);

        // 알림 발송
        
    }
}

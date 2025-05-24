package aiku_main.service.strategy;

import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.kafka.KafkaProducerService;
import common.domain.value_reference.MemberValue;
import common.kafka_message.KafkaTopic;
import common.kafka_message.PointChangedType;
import common.kafka_message.RacingPointChangedFailedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static common.kafka_message.KafkaTopic.ALARM;

@RequiredArgsConstructor
@PointChangeReasonMapping({PointChangeReason.RACING, PointChangeReason.RACING_REWARD, PointChangeReason.RACING_CANCEL})
@Component
public class RacingRollbackStrategy implements RollbackStrategy {

    private final KafkaProducerService kafkaProducerService;

    @Override
    public void execute(Long memberId, PointChangeType pointChangeType, int pointAmount, PointChangeReason pointChangeReason, Long reasonId) {
        kafkaProducerService.sendMessage(ALARM,
                new RacingPointChangedFailedMessage(
                        memberId,
                        PointChangedType.valueOf(pointChangeType.name()),
                        pointAmount,
                        common.kafka_message.PointChangeReason.valueOf(pointChangeReason.name()),
                        reasonId
                )
        );
    }
}

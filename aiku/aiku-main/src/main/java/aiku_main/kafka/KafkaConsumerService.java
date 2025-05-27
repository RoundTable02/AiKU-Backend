package aiku_main.kafka;

import aiku_main.application_event.event.PointChangeEvent;
import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.service.schedule.ScheduleService;
import common.kafka_message.ScheduleArrivalMessage;
import common.kafka_message.ScheduleCloseMessage;
import common.util.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaConsumerService {

    private final ScheduleService scheduleService;
    private final ApplicationEventPublisher eventPublisher;

    @KafkaListener(topics = {"test"}, groupId = "test", concurrency = "1")
    public void consumeTest(ConsumerRecord<String, String> data, Acknowledgment ack) {
        log.info("KafkaConsumerService Test : value = {}", data.value());
        ack.acknowledge();
    }

    @KafkaListener(topics = {"schedule-close"}, groupId = "aiku-main", concurrency = "1")
    public void consumeScheduleClose(ConsumerRecord<String, String> data, Acknowledgment ack) {
        ScheduleCloseMessage message = ObjectMapperUtil.parseJson(data.value(), ScheduleCloseMessage.class);
        scheduleService.closeSchedule(message.getScheduleId(), message.getScheduleCloseTime());

        ack.acknowledge();
    }

    @KafkaListener(topics = {"point-change"}, groupId = "aiku-main", concurrency = "1")
    public void consumePointChangeEvent(ConsumerRecord<String, String> data, Acknowledgment ack) {
        PointChangeEvent message = ObjectMapperUtil.parseJson(data.value(), PointChangeEvent.class);
        publishPointChangeEvent(message.getMemberId(),
                message.getSign(),
                message.getPointAmount(),
                message.getReason(),
                message.getReasonId()
        );

        ack.acknowledge();
    }

    private void publishPointChangeEvent(Long memberId, PointChangeType changeType, int pointAmount, PointChangeReason reason, Long reasonId){
        PointChangeEvent event = new PointChangeEvent(
                memberId,
                changeType,
                pointAmount,
                reason,
                reasonId
        );
        eventPublisher.publishEvent(event);
    }
}

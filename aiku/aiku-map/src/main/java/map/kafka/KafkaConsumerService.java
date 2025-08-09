package map.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.kafka_message.RacingPointChangedFailedMessage;
import common.kafka_message.ScheduleCloseMessage;
import common.util.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import map.application_event.event.ScheduleAutoCloseEvent;
import map.application_event.event.ScheduleCloseEvent;
import map.service.RacingSagaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaConsumerService {

    private final RacingSagaService racingSagaService;
    private final ApplicationEventPublisher publisher;

    @KafkaListener(topics = {"racing-point-failed"}, groupId = "aiku-main", concurrency = "1")
    public void consumeRacingPointChangedFailedMessage(ConsumerRecord<String, String> data, Acknowledgment ack) {
        RacingPointChangedFailedMessage message = ObjectMapperUtil.parseJson(data.value(), RacingPointChangedFailedMessage.class);
        racingSagaService.rollbackRacing(message.getMemberId(), message.getPointChangedType(), message.getPointAmount(), message.getReason(), message.getReasonId());

        ack.acknowledge();
    }

    @KafkaListener(topics = {"schedule-auto-close"}, groupId = "aiku-main", concurrency = "1")
    public void consumeScheduleClose(ConsumerRecord<String, String> data, Acknowledgment ack) {
        ScheduleCloseMessage message = ObjectMapperUtil.parseJson(data.value(), ScheduleCloseMessage.class);
        ScheduleAutoCloseEvent event = new ScheduleAutoCloseEvent(message.getScheduleId(), message.getScheduleCloseTime());
        publisher.publishEvent(event);

        ack.acknowledge();
    }


}

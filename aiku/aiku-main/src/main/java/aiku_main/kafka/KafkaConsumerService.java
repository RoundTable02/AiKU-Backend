package aiku_main.kafka;

import aiku_main.application_event.event.PointChangeEvent;
import aiku_main.application_event.event.PointChangeReason;
import aiku_main.application_event.event.PointChangeType;
import aiku_main.application_event.publisher.PointChangeEventPublisher;
import aiku_main.service.ScheduleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.domain.value_reference.MemberValue;
import common.kafka_message.PaymentPointChangedMessage;
import common.kafka_message.ScheduleArrivalMessage;
import common.kafka_message.ScheduleCloseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaConsumerService {

    private final ScheduleService scheduleService;
    private final ObjectMapper objectMapper;

    private final PointChangeEventPublisher pointChangeEventPublisher;

    @KafkaListener(topics = {"test"}, groupId = "test", concurrency = "1")

    public void consumeTest(ConsumerRecord<String, String> data, Acknowledgment ack) {
        log.info("KafkaConsumerService Test : value = {}", data.value());
        ack.acknowledge();
    }

    @KafkaListener(topics = {"schedule-close"}, groupId = "aiku-main", concurrency = "1")
    public void consumeScheduleClose(ConsumerRecord<String, String> data, Acknowledgment ack) {
        try {
            ScheduleCloseMessage message = objectMapper.readValue(data.value(), ScheduleCloseMessage.class);
            scheduleService.closeSchedule(message.getScheduleId(), message.getScheduleCloseTime());
        } catch (JsonMappingException e) {
            log.error("KafkaConsumerService.consumeScheduleClose에서 ScheduleCloseMessage파싱 오류가 발생하였습니다. message = {}", data.value(), e);
        } catch (JsonProcessingException e) {
            log.error("KafkaConsumerService.consumeScheduleClose에서 ScheduleCloseMessage파싱 오류가 발생하였습니다. message = {}", data.value(), e);
        }

        ack.acknowledge();
    }

    @KafkaListener(topics = {"schedule-arrival"}, groupId = "aiku-main", concurrency = "1")
    public void consumeScheduleArrival(ConsumerRecord<String, String> data, Acknowledgment ack) {
        try {
            ScheduleArrivalMessage message = objectMapper.readValue(data.value(), ScheduleArrivalMessage.class);
            scheduleService.arriveSchedule(message.getScheduleId(), message.getMemberId(), message.getArrivalTime());
        } catch (JsonMappingException e) {
            log.error("KafkaConsumerService.consumeScheduleArrival에서 ScheduleArrivalMessage파싱 오류가 발생하였습니다. message = {}", data.value(), e);
        } catch (JsonProcessingException e) {
            log.error("KafkaConsumerService.consumeScheduleArrival에서 ScheduleArrivalMessage파싱 오류가 발생하였습니다. message = {}", data.value(), e);
        }
    }

    @KafkaListener(topics = {"point-change"}, groupId = "aiku-main", concurrency = "1")
    public void consumePointChangeEvent(ConsumerRecord<String, String> data, Acknowledgment ack) {
        try {
            PointChangeEvent message = objectMapper.readValue(data.value(), PointChangeEvent.class);
            pointChangeEventPublisher.consumerPublish(message.getMember(),
                    message.getSign(),
                    message.getPointAmount(),
                    message.getReason(),
                    message.getReasonId()
            );
        } catch (JsonMappingException e) {
            log.error("KafkaConsumerService.consumeScheduleClose에서 ScheduleCloseMessage파싱 오류가 발생하였습니다. message = {}", data.value(), e);
        } catch (JsonProcessingException e) {
            log.error("KafkaConsumerService.consumeScheduleClose에서 ScheduleCloseMessage파싱 오류가 발생하였습니다. message = {}", data.value(), e);
        }

        ack.acknowledge();
    }
}

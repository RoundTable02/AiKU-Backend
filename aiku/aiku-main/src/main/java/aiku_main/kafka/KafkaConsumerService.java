package aiku_main.kafka;

import aiku_main.service.ScheduleService;
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

    @KafkaListener(topics = {"test"}, groupId = "test", concurrency = "1")
    public void consumeTest(ConsumerRecord<String, String> data, Acknowledgment ack){
        log.info("KafkaConsumerService Test : value = {}", data.value());
        ack.acknowledge();
    }

    @KafkaListener(topics = {"schedule-close"}, groupId = "aiku-main", concurrency = "1")
    public void consumeScheduleClose(ConsumerRecord<String, String> data, Acknowledgment ack){

        ack.acknowledge();
    }
}

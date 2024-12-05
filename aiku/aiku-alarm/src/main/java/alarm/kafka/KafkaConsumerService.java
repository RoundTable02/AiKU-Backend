package alarm.kafka;

import alarm.service.MemberMessageService;
import alarm.util.AlarmMessageMapper;
import common.kafka_message.alarm.AlarmMessage;
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

    private final MemberMessageService messageService;
    private final AlarmMessageMapper messageMapper;

    @KafkaListener(topics = {"alarm"}, groupId = "aiku-alarm", concurrency = "1")
    public void consumeAlarmEvent(ConsumerRecord<String, String> data, Acknowledgment ack){
        AlarmMessage message = messageMapper.mapToAlarmMessage(data);
        messageService.sendAndSaveMessage(message);

        ack.acknowledge();
    }
}

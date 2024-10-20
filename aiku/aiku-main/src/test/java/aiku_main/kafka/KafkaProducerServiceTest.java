package aiku_main.kafka;

import common.kafka_message.alarm.AlarmMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static common.kafka_message.alarm.AlarmMessageType.TEST;
import static common.kafka_message.KafkaTopic.alarm;

@Transactional
@SpringBootTest
class KafkaProducerServiceTest {

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Test
    void sendMessage() {
        //given
        AlarmMessage message = new AlarmMessage(1L, "token", 1L, TEST);

        //when
        kafkaProducerService.sendMessage(alarm, message);
    }
}

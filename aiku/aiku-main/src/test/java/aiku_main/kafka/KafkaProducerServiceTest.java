package aiku_main.kafka;

import common.kafka.AlarmMessage;
import common.kafka.AlarmMessageType;
import common.kafka.KafkaTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static common.kafka.AlarmMessageType.TEST;
import static common.kafka.KafkaTopic.alarm;
import static org.junit.jupiter.api.Assertions.*;

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
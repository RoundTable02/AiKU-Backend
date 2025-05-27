package aiku_main.kafka;

import common.kafka_message.KafkaTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static common.kafka_message.KafkaTopic.TEST;

@SpringBootTest
class KafkaConsumerServiceTest {

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Test
    void consumeTest() throws InterruptedException {
        //given
        String message = "testMessage";
        kafkaProducerService.sendMessage(TEST, message);

        //given
        Thread.sleep(5000);
    }
}
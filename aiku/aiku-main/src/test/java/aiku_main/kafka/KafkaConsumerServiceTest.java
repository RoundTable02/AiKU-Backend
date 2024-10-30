package aiku_main.kafka;

import common.kafka_message.KafkaTopic;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KafkaConsumerServiceTest {

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Test
    void consumeTest() throws InterruptedException {
        //given
        String message = "testMessage";
        kafkaProducerService.sendMessage(KafkaTopic.test, message);

        //given
        Thread.sleep(5000);
    }
}
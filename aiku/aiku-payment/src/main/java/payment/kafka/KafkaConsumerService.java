package payment.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.kafka_message.PaymentPointSagaMessage;
import common.kafka_message.PaymentPointStatus;
import common.kafka_message.ScheduleArrivalMessage;
import common.kafka_message.ScheduleCloseMessage;
import common.kafka_message.alarm.PaymentSuccessMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import payment.service.PaymentService;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaConsumerService {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {"test"}, groupId = "test", concurrency = "1")
    public void consumeTest(ConsumerRecord<String, String> data, Acknowledgment ack){
        log.info("KafkaConsumerService Test : value = {}", data.value());
        ack.acknowledge();
    }

    @KafkaListener(topics = {"payment"}, groupId = "aiku-payment", concurrency = "1")
    public void consumeSagaMessage(ConsumerRecord<String, String> data, Acknowledgment ack){
        try {
            PaymentPointSagaMessage message = objectMapper.readValue(data.value(), PaymentPointSagaMessage.class);
            if (message.getStatus().equals(PaymentPointStatus.SUCCESS)) {
                paymentService.consumePurchase(message.getPurchaseToken());
            }
            else {
                paymentService.pointChargeFailed(message.getPurchaseToken());
            }
        } catch (JsonMappingException e) {
            log.error("KafkaConsumerService.consumeSagaMessage에서 PaymentSuccessMessage파싱 오류가 발생하였습니다. message = {}", data.value(), e);
        } catch (JsonProcessingException e) {
            log.error("KafkaConsumerService.consumeSagaMessage에서 PaymentSuccessMessage파싱 오류가 발생하였습니다. message = {}", data.value(), e);
        }

        ack.acknowledge();
    }
}

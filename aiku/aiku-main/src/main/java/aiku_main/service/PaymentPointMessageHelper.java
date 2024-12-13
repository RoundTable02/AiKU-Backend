package aiku_main.service;

import aiku_main.kafka.KafkaProducerService;
import common.kafka_message.KafkaTopic;
import common.kafka_message.PaymentPointSagaMessage;
import common.kafka_message.PaymentPointStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaymentPointMessageHelper {

    private final KafkaProducerService kafkaProducerService;

    public void makePaymentPointChangeSagaMessage(PaymentPointStatus status, String purchaseToken) {
        kafkaProducerService.sendMessage(KafkaTopic.alarm,
                new PaymentPointSagaMessage(status, purchaseToken)
        );
    }
}

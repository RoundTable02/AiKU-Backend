package common.kafka_message;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PaymentPointSagaMessage {

    private PaymentPointStatus status;
    private String purchaseToken;

    public PaymentPointSagaMessage(PaymentPointStatus status, String purchaseToken) {
        this.status = status;
        this.purchaseToken = purchaseToken;
    }
}

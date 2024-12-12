package common.kafka_message.alarm;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class PaymentSuccessMessage extends AlarmMessage {

    private String purchaseToken;
    private String price;
    private String point;

    public PaymentSuccessMessage(List<String> alarmReceiverTokens, AlarmMessageType alarmMessageType, String purchaseToken, String price, String point) {
        super(alarmReceiverTokens, alarmMessageType);
        this.purchaseToken = purchaseToken;
        this.price = price;
        this.point = point;
    }
}

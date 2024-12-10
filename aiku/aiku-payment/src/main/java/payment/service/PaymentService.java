package payment.service;

import com.google.api.services.androidpublisher.model.ProductPurchase;
import common.domain.Payment;
import common.domain.PaymentProduct;
import common.domain.PaymentProductType;
import common.kafka_message.KafkaTopic;
import common.kafka_message.PointChangeReason;
import common.kafka_message.PointChangedMessage;
import common.kafka_message.PointChangedType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payment.exception.PaymentException;
import payment.kafka.KafkaProducerService;
import payment.repository.PaymentProductRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PaymentService {

    private final PaymentProductRepository paymentProductRepository;
    private final PaymentServiceHelper paymentServiceHelper;
    private final GooglePaymentHelper googlePaymentHelper;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public Long verifyProductPurchase(Long memberId, PaymentProductType type, String packageName, String productId, String purchaseToken) {
        // purchaseToken 유일성 체크
        validateDB(purchaseToken);

        // Payment 생성을 위한 내부 트랜잭션 생성
        PaymentProduct paymentProduct = getPaymentProduct(type);
        paymentServiceHelper.makePayment(paymentProduct, memberId, purchaseToken);

        // Google 인앱 결제 영수증 검증
        ProductPurchase purchase = googlePaymentHelper.verifyProductPurchase(packageName, productId, purchaseToken);

        if (purchase.getPurchaseState() == 0 && purchase.getConsumptionState() == 0) {
            // 소비 처리
            googlePaymentHelper.consumeProduct(packageName, productId, purchaseToken);
            acceptPayment(paymentProduct, purchaseToken);
        }

        makePointEvent(memberId, type, purchaseToken);

        return memberId;
    }

    private void makePointEvent(Long memberId, PaymentProductType type, String purchaseToken) {
        Payment payment = getPaymentByPurchaseToken(purchaseToken);

        // 아쿠 증가 이벤트
        kafkaProducerService.sendMessage(KafkaTopic.alarm,
                new PointChangedMessage(memberId,
                        PointChangedType.PLUS,
                        type.getPoint(),
                        PointChangeReason.RACING,
                        payment.getId()
                )
        );
    }

    private Payment getPaymentByPurchaseToken(String purchaseToken) {
        return paymentProductRepository.findPaymentByPaymentPurchaseToken(purchaseToken)
                .orElseThrow(() -> new PaymentException());
    }

    private void acceptPayment(PaymentProduct paymentProduct, String purchaseToken) {
        // Payment 상태 업데이트
        paymentProduct.acceptPayment(purchaseToken);
    }

    private PaymentProduct getPaymentProduct(PaymentProductType type) {
        PaymentProduct paymentProduct = paymentProductRepository.findByPaymentProductType(type)
                .orElseThrow(PaymentException::new);

        return paymentProduct;
    }

    private void validateDB(String purchaseToken) {
        // DB 내 purchaseToken 중복되는지 확인, false -> 유일 보장
        if (paymentProductRepository.existsByPurchaseToken(purchaseToken)) {
            throw new PaymentException();
        }
    }

    // TODO : 보상 트랜잭션; 사용자 아쿠 증가 성공 시 알림
    public void pointChargeSuccessAlarm() {

    }

    // TODO : 보상 트랜잭션; 사용자 아쿠 증가 실패 시 환불 처리
    public void refund() {
        pointChargeFailAlarm();
    }

    // TODO : 보상 트랜잭션; 사용자 아쿠 증가 실패 시 실패 알림
    public void pointChargeFailAlarm() {

    }

}

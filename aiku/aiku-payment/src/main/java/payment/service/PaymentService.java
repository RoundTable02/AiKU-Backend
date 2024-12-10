package payment.service;

import com.google.api.services.androidpublisher.model.ProductPurchase;
import common.domain.PaymentProduct;
import common.domain.PaymentProductType;
import common.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import payment.exception.MemberNotFoundException;
import payment.exception.PaymentException;
import payment.repository.MemberRepository;
import payment.repository.PaymentProductRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PaymentService {

    private final PaymentProductRepository paymentProductRepository;
    private final MemberRepository memberRepository;
    private final GooglePaymentHelper googlePaymentHelper;

    @Transactional
    public Long verifyProductPurchase(Long memberId, PaymentProductType type, String packageName, String productId, String purchaseToken) {
        validateDB(purchaseToken);

        PaymentProduct paymentProduct = getPaymentProduct(type);

        makePayment(memberId, purchaseToken, paymentProduct);

        ProductPurchase purchase = googlePaymentHelper.verifyProductPurchase(packageName, productId, purchaseToken);

        if (purchase.getPurchaseState() == 0 && purchase.getConsumptionState() == 0) {
            // 소비 처리
            googlePaymentHelper.consumeProduct(packageName, productId, purchaseToken);
            acceptPayment(paymentProduct, purchaseToken);
        }

        // TODO : 아쿠 증가 이벤트

        return memberId;
    }

    private void acceptPayment(PaymentProduct paymentProduct, String purchaseToken) {
        // DB Payment 상태 업데이트
        paymentProduct.acceptPayment(purchaseToken);
    }

    private PaymentProduct getPaymentProduct(PaymentProductType type) {
        PaymentProduct paymentProduct = paymentProductRepository.findByPaymentProductType(type)
                .orElseThrow(PaymentException::new);

        return paymentProduct;
    }

    private void makePayment(Long memberId, String purchaseToken, PaymentProduct paymentProduct) {
        int price = paymentProduct.getPaymentProductType().getPrice();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        paymentProduct.makePayment(member, price, purchaseToken);
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

    }

    // TODO : 보상 트랜잭션; 사용자 아쿠 증가 실패 시 실패 알림
    public void pointChargeFailAlarm() {

    }

}

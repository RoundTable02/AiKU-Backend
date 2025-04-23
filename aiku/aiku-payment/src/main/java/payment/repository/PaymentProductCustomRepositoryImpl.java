package payment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.payment.Payment;
import common.domain.payment.QPayment;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static common.domain.payment.QPaymentProduct.paymentProduct;


@RequiredArgsConstructor
public class PaymentProductCustomRepositoryImpl implements PaymentProductCustomRepository {

    private final JPAQueryFactory query;

    @Override
    public Boolean existsByPurchaseToken(String purchaseToken) {
        QPayment payment = new QPayment("payment");

        Long count = query.select(payment.count())
                .where(payment.purchaseToken.eq(purchaseToken))
                .fetchOne();

        return count != null && count > 0;
    }

    @Override
    public Optional<Payment> findPaymentByPaymentPurchaseToken(String purchaseToken) {
        QPayment payment = new QPayment("payment");

        Payment paymentResult = query.select(payment)
                .from(paymentProduct)
                .leftJoin(paymentProduct.payments, payment)
                .where(payment.purchaseToken.eq(purchaseToken))
                .fetchOne();

        return Optional.ofNullable(paymentResult);
    }
}

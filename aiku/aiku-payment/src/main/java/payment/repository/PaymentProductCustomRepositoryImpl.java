package payment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import common.domain.QPayment;
import lombok.RequiredArgsConstructor;

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
}

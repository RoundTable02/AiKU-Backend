package payment.repository;

import common.domain.Payment;

import java.util.Optional;

public interface PaymentProductCustomRepository {

    Boolean existsByPurchaseToken(String purchaseToken);

    Optional<Payment> findPaymentByPaymentPurchaseToken(String purchaseToken);
}

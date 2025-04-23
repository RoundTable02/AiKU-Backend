package payment.repository;

import common.domain.payment.PaymentProduct;
import common.domain.payment.PaymentProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentProductRepository extends JpaRepository<PaymentProduct, Long>, PaymentProductCustomRepository {

    Optional<PaymentProduct> findByPaymentProductType(PaymentProductType type);
}

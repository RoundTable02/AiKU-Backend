package payment.repository;

public interface PaymentProductCustomRepository {

    Boolean existsByPurchaseToken(String purchaseToken);

}

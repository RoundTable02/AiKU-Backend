package aiku_payment.payment_test;

import common.domain.Payment;
import common.domain.PaymentProduct;
import common.domain.PaymentProductType;
import common.domain.member.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import payment.SpringPaymentApplication;
import payment.repository.PaymentProductRepository;

import java.util.List;

@Transactional
@SpringBootTest(classes = SpringPaymentApplication.class)
public class PaymentServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    PaymentProductRepository repository;

    Member member;

    @BeforeEach
    void before() {
        PaymentProduct product = PaymentProduct.builder()
                .productId("abc")
                .paymentProductType(PaymentProductType.PRODUCT01)
                .build();

        repository.save(product);

        member = Member.create("abc");

        em.persist(member);
    }

    @Test
    void 테스트() {

        PaymentProduct paymentProduct = repository.findByPaymentProductType(PaymentProductType.PRODUCT01)
                .orElseThrow();

        paymentProduct.makePayment(member.getId(), paymentProduct.getPaymentProductType().getPrice(), "1234");

        repository.save(paymentProduct);

        List<Payment> payments = paymentProduct.getPayments();

        System.out.println("payments.get(0).getId() = " + payments.get(0).getId());
    }
}

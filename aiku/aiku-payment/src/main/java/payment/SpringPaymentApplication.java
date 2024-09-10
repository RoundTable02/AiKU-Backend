package payment;

import common.TestBean;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {"payment", "common"})
public class SpringPaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringPaymentApplication.class);
    }

    private final TestBean testBean;
    @Autowired
    public SpringPaymentApplication(TestBean testBean) {
        this.testBean = testBean;
    }
    @PostConstruct
    public void postConstruct() {
        testBean.loadBean();
    }
}

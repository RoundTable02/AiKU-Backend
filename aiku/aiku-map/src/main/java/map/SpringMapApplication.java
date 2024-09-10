package map;

import common.TestBean;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {"map", "common"})
public class SpringMapApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringMapApplication.class);
    }

    private final TestBean testBean;
    @Autowired
    public SpringMapApplication(TestBean testBean) {
        this.testBean = testBean;
    }
    @PostConstruct
    public void postConstruct() {
        testBean.loadBean();
    }
}

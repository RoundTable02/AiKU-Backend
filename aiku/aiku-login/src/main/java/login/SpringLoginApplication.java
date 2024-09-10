package login;

import common.TestBean;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {"login", "common"})
public class SpringLoginApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringLoginApplication.class);
    }

    private final TestBean testBean;
    @Autowired
    public SpringLoginApplication(TestBean testBean) {
        this.testBean = testBean;
    }
    @PostConstruct
    public void postConstruct(){
        testBean.loadBean();
    }
}

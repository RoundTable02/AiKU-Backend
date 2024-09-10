package aiku_main;

import common.TestBean;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {"aiku_main", "common"})
public class SpringMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringMainApplication.class);
    }

    private final TestBean testBean;
    @Autowired
    public SpringMainApplication(TestBean testBean) {
        this.testBean = testBean;
    }
    @PostConstruct
    public void postConstruct(){
        testBean.loadBean();
    }
}

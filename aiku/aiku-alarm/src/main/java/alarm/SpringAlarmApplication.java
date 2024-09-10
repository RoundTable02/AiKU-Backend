package alarm;

import common.TestBean;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = {"alarm", "common"})
public class SpringAlarmApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringAlarmApplication.class, args);
    }

    private final TestBean testBean;
    @Autowired
    public SpringAlarmApplication(TestBean testBean) {
        this.testBean = testBean;
    }
    @PostConstruct
    public void postConstruct(){
        testBean.loadBean();
    }
}

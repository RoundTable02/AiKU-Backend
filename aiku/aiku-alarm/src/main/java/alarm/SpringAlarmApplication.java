package alarm;

import com.querydsl.jpa.impl.JPAQueryFactory;
import common.TestBean;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EntityScan(basePackages = {"common"})
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

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }
}

package map;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@EnableJpaAuditing
@EntityScan(basePackages = {"common"})
@SpringBootApplication(scanBasePackages = {"map", "common"})
public class SpringMapApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMapApplication.class);
    }
}

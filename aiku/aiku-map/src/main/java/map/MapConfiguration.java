package map;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class MapConfiguration {
    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5); // 스레드 풀의 크기 설정
        scheduler.setThreadNamePrefix("scheduled-task-"); // 스레드 이름 접두사 설정
        scheduler.setWaitForTasksToCompleteOnShutdown(true); // 애플리케이션 종료 시 남아 있는 작업 완료 대기 여부
        scheduler.setAwaitTerminationSeconds(30); // 종료 시 대기 시간(초)
        return scheduler;
    }
}

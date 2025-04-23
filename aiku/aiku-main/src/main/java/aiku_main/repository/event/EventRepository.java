package aiku_main.repository.event;

import common.domain.event.CommonEvent;
import common.domain.event.RecommendEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<CommonEvent, Long> {
    Optional<RecommendEvent> findRecommendEventByMemberId(Long memberId);
}

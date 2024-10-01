package login.repository;

import common.domain.event.CommonEvent;
import common.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventRepository extends JpaRepository<CommonEvent, Long> {
}

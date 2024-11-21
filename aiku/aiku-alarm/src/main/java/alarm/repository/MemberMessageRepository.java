package alarm.repository;

import alarm.domain.MemberMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberMessageRepository extends JpaRepository<MemberMessage, Long> {

    List<MemberMessage> findAllByMemberId(Long memberId);
}

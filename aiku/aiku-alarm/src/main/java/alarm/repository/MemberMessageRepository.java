package alarm.repository;

import common.domain.MemberMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberMessageRepository extends JpaRepository<MemberMessage, Long> {

    @Query("select m from MemberMessage m where m.memberId = :memberId and m.createdAt between :startDate and :now")
    List<MemberMessage> findAllByMemberId(Long memberId, LocalDateTime startDate, LocalDateTime now, Pageable page);
}

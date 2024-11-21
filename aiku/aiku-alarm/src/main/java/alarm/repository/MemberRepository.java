package alarm.repository;

import common.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m.id from Member m where m.firebaseToken in :firebaseTokens")
    List<Long> findMemberIdsByFirebaseTokenList(List<String> firebaseTokens);
}

package map.repository;

import common.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    @Query("select m.firebaseToken from Member m where m.id = ?1")
    Optional<String> findMemberFirebaseTokenById(Long memberId);
}

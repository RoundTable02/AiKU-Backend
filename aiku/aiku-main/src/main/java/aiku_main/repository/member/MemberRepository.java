package aiku_main.repository.member;

import common.domain.Status;
import common.domain.member.Member;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByIdAndStatus(Long memberId, Status status);
    Optional<Member> findByNickname(String recommenderNickname);

    Optional<Member> findByKakaoId(Long aLong);

    boolean existsByNickname(String nickname);

    @Query("SELECT m.firebaseToken FROM Member m WHERE m.id = :memberId")
    Optional<String> findFirebaseTokenByMemberId(Long memberId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    @Query("SELECT m FROM Member m WHERE m.id = :memberId")
    Optional<Member> findByMemberIdForUpdate(Long memberId);
}

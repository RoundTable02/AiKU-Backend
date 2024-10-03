package login.repository;

import common.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberReadRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByKakaoId(Long kakaoId);

    boolean existsByNickname(String nickname);
}

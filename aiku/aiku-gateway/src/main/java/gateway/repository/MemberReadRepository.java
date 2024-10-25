package gateway.repository;

import gateway.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberReadRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByKakaoId(Long kakaoId);

    boolean existsByNickname(String nickname);
}

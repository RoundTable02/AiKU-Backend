package gateway.repository;

import gateway.dto.member.Member;

import java.util.Optional;

public interface MemberQueryRepository {

    Optional<Member> findMemberByKakaoId(Long kakaoId);
    boolean existsByNickname(String nickname);
}

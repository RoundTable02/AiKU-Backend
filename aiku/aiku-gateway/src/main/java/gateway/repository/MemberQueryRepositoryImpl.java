package gateway.repository;

import gateway.dto.member.Member;
import gateway.dto.member.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class MemberQueryRepositoryImpl implements MemberQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Member> memberRowMapper = (rs, rowNum) -> new Member(
            rs.getLong("member_id"),
            rs.getLong("kakao_id"),
            rs.getString("refresh_token"),
            rs.getString("nickname"),
            rs.getString("email"),
            rs.getString("password"),
            MemberRole.valueOf(rs.getString("role"))
    );

    @Override
    public Optional<Member> findMemberByKakaoId(Long kakaoId) {
        String sql = "SELECT * FROM member WHERE kakao_id = ?";
        return jdbcTemplate.query(sql, memberRowMapper, kakaoId)
                .stream()
                .findFirst();
    }

    @Override
    public boolean existsByNickname(String nickname) {
        String sql = "SELECT COUNT(*) FROM member WHERE nickname = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, nickname);
        return count != null && count > 0;
    }
}

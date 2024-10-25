package gateway.security;

import gateway.domain.member.Member;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
@Slf4j
public class MemberAdaptor extends User {
    private Member member;

    public MemberAdaptor(Member member, List<SimpleGrantedAuthority> authorities) {
        super(String.valueOf(member.getKakaoId()), member.getPassword(), authorities);
        this.member = member;
    }
}

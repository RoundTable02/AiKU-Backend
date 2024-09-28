package login.security;

import common.domain.member.Member;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;

@Getter
@Slf4j
public class MemberAdaptor extends User {
    private Member member;

    public MemberAdaptor(Member member) {
        super(member.getEmail(), member.getPassword(), member.getAuthorities());
        this.member = member;
    }
}

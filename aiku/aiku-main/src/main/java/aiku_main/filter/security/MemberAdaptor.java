package aiku_main.filter.security;

import common.domain.member.Member;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
@Slf4j
public class MemberAdaptor extends User {
    private Member member;

    public MemberAdaptor(Member member) {
        super(String.valueOf(member.getKakaoId()), member.getPassword(), List.of(new SimpleGrantedAuthority(member.getRole().name())));
        this.member = member;
    }
}

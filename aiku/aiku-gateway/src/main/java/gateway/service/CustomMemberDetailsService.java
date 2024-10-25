package gateway.service;

import gateway.dto.member.Member;
import gateway.exception.MemberNotFoundException;
import gateway.repository.MemberQueryRepository;
import gateway.security.MemberAdaptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {

    private final MemberQueryRepository memberQueryRepository;

    @Override
    public UserDetails loadUserByUsername(String kakaoId) throws UsernameNotFoundException {
        Member member = memberQueryRepository.findMemberByKakaoId(Long.parseLong(kakaoId))
                .orElseThrow(() -> new MemberNotFoundException());

        // Member Role을 리스트 형태로 전달
        return new MemberAdaptor(member, List.of(new SimpleGrantedAuthority(member.getRole().name())));
    }
}

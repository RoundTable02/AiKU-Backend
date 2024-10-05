package login.service;

import common.domain.member.Member;
import login.exception.MemberNotFoundException;
import login.repository.MemberReadRepository;
import login.security.MemberAdaptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomMemberDetailsService implements UserDetailsService {

    private final MemberReadRepository memberReadRepository;

    @Override
    public UserDetails loadUserByUsername(String kakaoId) throws UsernameNotFoundException {
        Member member = memberReadRepository.findMemberByKakaoId(Long.parseLong(kakaoId))
                .orElseThrow(() -> new MemberNotFoundException());

        return new MemberAdaptor(member);
    }
}

package aiku_main.service.member;

import aiku_main.dto.FirebaseTokenDto;
import aiku_main.exception.*;
import aiku_main.repository.member.MemberRepository;
import common.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberFirebaseService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long saveToken(Long accessMemberId, FirebaseTokenDto firebaseTokenDto) {
        Member member = memberRepository.findById(accessMemberId)
                .orElseThrow(() -> new MemberNotFoundException());

        member.updateFirebaseToken(firebaseTokenDto.getToken());

        return member.getId();
    }

}

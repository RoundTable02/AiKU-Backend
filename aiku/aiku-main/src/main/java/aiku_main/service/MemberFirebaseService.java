package aiku_main.service;

import aiku_main.dto.FirebaseTokenDto;
import aiku_main.dto.TermResDto;
import aiku_main.exception.FcmTokenDuplicateException;
import aiku_main.exception.MemberNotFoundException;
import aiku_main.exception.NoFcmTokenException;
import aiku_main.exception.NoSuchTermException;
import aiku_main.repository.MemberRepository;
import aiku_main.repository.TermRepository;
import common.domain.Term;
import common.domain.TermTitle;
import common.domain.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberFirebaseService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long saveToken(Long accessMemberId, FirebaseTokenDto firebaseTokenDto) {
        Member member = memberRepository.findById(accessMemberId)
                .orElseThrow(() -> new MemberNotFoundException());

        if (!Objects.isNull(member.getFirebaseToken())) {
            throw new FcmTokenDuplicateException();
        }
        member.updateFirebaseToken(firebaseTokenDto.getToken());

        return member.getId();
    }

    @Transactional
    public Long updateToken(Long accessMemberId, FirebaseTokenDto firebaseTokenDto) {
        Member member = memberRepository.findById(accessMemberId)
                .orElseThrow(() -> new MemberNotFoundException());

        if (Objects.isNull(member.getFirebaseToken())) {
            throw new NoFcmTokenException();
        }
        member.updateFirebaseToken(firebaseTokenDto.getToken());

        return member.getId();
    }
}

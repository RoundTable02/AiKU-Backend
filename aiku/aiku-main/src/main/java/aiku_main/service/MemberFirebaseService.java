package aiku_main.service;

import aiku_main.dto.FirebaseTokenDto;
import aiku_main.dto.TermResDto;
import aiku_main.exception.*;
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

import static common.response.status.BaseErrorCode.DUPLICATED_FCM_TOKEN;
import static common.response.status.BaseErrorCode.NO_FCM_TOKEN;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberFirebaseService {

    private final MemberRepository memberRepository;

    @Transactional
    public Long saveToken(Long accessMemberId, FirebaseTokenDto firebaseTokenDto) {
        Member member = memberRepository.findById(accessMemberId)
                .orElseThrow(() -> new MemberNotFoundException());

        if (!member.getFirebaseToken().equals("NOT_DEFINED")) {
            throw new FcmException(DUPLICATED_FCM_TOKEN);
        }
        member.updateFirebaseToken(firebaseTokenDto.getToken());

        return member.getId();
    }

    @Transactional
    public Long updateToken(Long accessMemberId, FirebaseTokenDto firebaseTokenDto) {
        Member member = memberRepository.findById(accessMemberId)
                .orElseThrow(() -> new MemberNotFoundException());

        if (member.getFirebaseToken().equals("NOT_DEFINED")) {
            throw new FcmException(NO_FCM_TOKEN);
        }
        member.updateFirebaseToken(firebaseTokenDto.getToken());

        return member.getId();
    }
}

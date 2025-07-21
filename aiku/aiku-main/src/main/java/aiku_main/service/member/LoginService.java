package aiku_main.service.member;

import aiku_main.dto.member.login.RefreshTokenResDto;
import aiku_main.dto.member.login.SignInTokenResDto;
import aiku_main.exception.JwtAccessDeniedException;
import aiku_main.exception.MemberNotFoundException;
import aiku_main.oauth.AppleOauthHelper;
import aiku_main.oauth.KakaoOauthHelper;
import aiku_main.oauth.OauthInfo;
import aiku_main.repository.member.MemberRepository;
import aiku_main.filter.security.JwtToken;
import aiku_main.filter.security.JwtTokenProvider;
import common.domain.member.Member;
import common.domain.member.OauthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class LoginService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final KakaoOauthHelper kakaoOauthHelper;
    private final AppleOauthHelper appleOauthHelper;
    private final MemberRepository memberRepository;

    /**
     * Payload를 통한 인증
     * ID 토큰의 영역 구분자인 온점(.)을 기준으로 헤더, 페이로드, 서명을 분리
     * 페이로드를 Base64 방식으로 디코딩
     * 페이로드의 iss 값이 https://kauth.kakao.com와 일치하는지 확인
     * 페이로드의 aud 값이 서비스 앱 키와 일치하는지 확인
     * 페이로드의 exp 값이 현재 UNIX 타임스탬프(Timestamp)보다 큰 값인지 확인(ID 토큰이 만료되지 않았는지 확인)
     * 페이로드의 nonce 값이 카카오 로그인 요청 시 전달한 값과 일치하는지 확인
     *
     * 서명 검증
     * ID 토큰의 영역 구분자인 온점(.)을 기준으로 헤더, 페이로드, 서명을 분리
     * 헤더를 Base64 방식으로 디코딩
     * OIDC: 공개키 목록 조회하기 API로 카카오 인증 서버가 서명 시 사용하는 공개키 목록 조회
     * 공개키 목록에서 헤더의 kid에 해당하는 공개키 값 확인
     * 공개키는 일정 기간 캐싱(Caching)하여 사용할 것을 권장하며, 지나치게 빈번한 요청 시 요청이 차단될 수 있으므로 유의
     * JWT 서명 검증을 지원하는 라이브러리를 사용해 공개키로 서명 검증
     * 참고: OpenID Foundation, jwt.io
     * 라이브러리를 사용하지 않고 직접 서명 검증 구현 시, RFC7515 규격에 따라 서명 검증 과정 진행 가능
     * 출처 : https://developers.kakao.com/docs/latest/ko/kakaologin/utilize#oidc
     *
     * idToken 검증 후 DB 상 멤버 존재 여부 확인
     */
    @Transactional
    public SignInTokenResDto signInKakao(String idToken) {
        OauthInfo info = kakaoOauthHelper.getOauthInfoByIdToken(idToken);
        String kakaoId = info.getOid();

        Member member = memberRepository.findByProviderAndOauthId(OauthProvider.KAKAO, Long.valueOf(kakaoId))
                .orElseThrow(MemberNotFoundException::new);

        UsernamePasswordAuthenticationToken authenticationFilter
                = new UsernamePasswordAuthenticationToken(member.getId(), kakaoId);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationFilter);

        // JWT Token 발급
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        member.reissueRefreshToken(jwtToken.getRefreshToken());

        return SignInTokenResDto.toDto(jwtToken);
    }

    @Transactional
    public SignInTokenResDto signInApple(String idToken) {
        OauthInfo info = appleOauthHelper.getOauthInfoByIdToken(idToken);
        String appleId = info.getOid();
        Member member = memberRepository.findByProviderAndOauthId(OauthProvider.APPLE, Long.valueOf(appleId))
                .orElseThrow(MemberNotFoundException::new);

        UsernamePasswordAuthenticationToken authenticationFilter
                = new UsernamePasswordAuthenticationToken(member.getId(), appleId);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationFilter);

        // JWT Token 발급
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        member.reissueRefreshToken(jwtToken.getRefreshToken());

        return SignInTokenResDto.toDto(jwtToken);
    }


    /**
     * Refresh Token을 통한 Access Token 및 Refresh Token 재발급 로직
     * member의 DB상 Refresh Token과 쿠키의 Refresh Token이 같은지 확인,
     * 이후 Access Token 재발급과 동시에 Refresh Token 역시 재발급 (RTR 방식)
     */
    @Transactional
    public RefreshTokenResDto refreshToken(Long accessMemberId, String refreshToken) {
        Member member = memberRepository.findById(accessMemberId)
                .orElseThrow(MemberNotFoundException::new);

        if (member.getRefreshToken().equals(refreshToken)) {
            Long memberId = member.getId();
            UsernamePasswordAuthenticationToken authenticationFilter
                    = new UsernamePasswordAuthenticationToken(memberId, member.getOauthId());

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationFilter);

            // 토큰 생성
            JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

            // 바뀐 Refresh Token 저장 (RTR)
            member.reissueRefreshToken(jwtToken.getRefreshToken());

            return new RefreshTokenResDto(jwtToken.getGrantType(), jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        } else {
            throw new JwtAccessDeniedException("잘못된 리프레시 토큰입니다.");
        }
    }
}

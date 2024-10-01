package login.controller;

import common.response.BaseResponse;
import common.response.BaseResultDto;
import login.dto.RefreshTokenResDto;
import login.dto.SignInDto;
import login.dto.SignInTokenResDto;
import login.security.MemberAdaptor;
import login.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static common.response.status.BaseCode.POST;

@RequestMapping("/login")
@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/sign-in")
    public BaseResponse<SignInTokenResDto> signIn(@RequestBody SignInDto signInDto){
        SignInTokenResDto signInTokenResDto = loginService.signIn(signInDto.getIdToken());

        return new BaseResponse<>(signInTokenResDto, POST);
    }

    // TODO : 만료 토큰 넘어오는 경우 Filter 단에서 짤릴 수 있음, 안드로이드 HTTP 쿠키 없는지 확인
    @PostMapping("/refresh")
    public BaseResponse<RefreshTokenResDto> refreshToken(
            @AuthenticationPrincipal MemberAdaptor memberAdaptor,
            @CookieValue("refreshToken") String refreshToken){
        RefreshTokenResDto refreshTokenResDto = loginService.refreshToken(memberAdaptor.getMember(), refreshToken);

        return new BaseResponse<>(refreshTokenResDto, POST);
    }
}

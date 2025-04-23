package aiku_main.controller.member;

import aiku_main.dto.member.login.RefreshTokenDto;
import aiku_main.dto.member.login.RefreshTokenResDto;
import aiku_main.dto.member.login.SignInDto;
import aiku_main.dto.member.login.SignInTokenResDto;
import aiku_main.service.member.LoginService;
import common.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/login")
@RequiredArgsConstructor
@RestController
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/sign-in/kakao")
    public BaseResponse<SignInTokenResDto> signInKakao(@RequestBody @Valid SignInDto signInDto){
        SignInTokenResDto signInTokenResDto = loginService.signInKakao(signInDto.getIdToken());

        return new BaseResponse<>(signInTokenResDto);
    }

    @PostMapping("/sign-in/apple")
    public BaseResponse<SignInTokenResDto> signInApple(@RequestBody @Valid SignInDto signInDto){
        SignInTokenResDto signInTokenResDto = loginService.signInApple(signInDto.getIdToken());

        return new BaseResponse<>(signInTokenResDto);
    }

    @PostMapping("/refresh")
    public BaseResponse<RefreshTokenResDto> refreshToken(
            @RequestHeader(name = "Access-Member-Id") Long accessMemberId,
            @RequestBody @Valid RefreshTokenDto refreshToken){
        RefreshTokenResDto refreshTokenResDto = loginService.refreshToken(accessMemberId, refreshToken.getRefreshToken());

        return new BaseResponse<>(refreshTokenResDto);
    }
}

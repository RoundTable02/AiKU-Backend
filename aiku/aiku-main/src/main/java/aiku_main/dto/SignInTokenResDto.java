package aiku_main.dto;

import aiku_main.filter.security.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInTokenResDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;

    public static SignInTokenResDto toDto(JwtToken jwtToken) {
        return new SignInTokenResDto(
                jwtToken.getGrantType(),
                jwtToken.getAccessToken(),
                jwtToken.getRefreshToken()
        );
    }
}

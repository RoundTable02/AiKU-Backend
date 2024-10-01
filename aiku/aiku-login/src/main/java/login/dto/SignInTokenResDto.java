package login.dto;

import login.security.JwtToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
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

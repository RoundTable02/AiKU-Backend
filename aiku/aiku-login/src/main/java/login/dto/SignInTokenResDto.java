package login.dto;

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
}

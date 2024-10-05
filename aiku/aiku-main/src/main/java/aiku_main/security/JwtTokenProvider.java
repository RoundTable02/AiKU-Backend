package aiku_main.security;

import aiku_main.exception.JwtAccessDeniedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 유저 정보 -> Token 생성
    public JwtToken generateToken(Authentication authentication) {
        if (authentication == null)
            throw new JwtAccessDeniedException("자격 증명에 실패하였습니다.");

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = generateAccessToken(authentication, authorities);
        String refreshToken = generateRefreshToken();

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String generateAccessToken(Authentication authentication, String authorities) {
        Calendar accessTokenCal = Calendar.getInstance();
        accessTokenCal.setTime(new Date());
//        accessTokenCal.add(Calendar.DATE, 1);
        // AccessToken : 1일 후 만료
        accessTokenCal.add(Calendar.MONTH, 1);
        // 테스트용 AccessToken : 1달 후 만료
        Date accessTokenExpiresIn = accessTokenCal.getTime();
        
        return Jwts.builder()
                .setSubject(authentication.getName()) // email
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken() {
        Calendar refreshTokenCal = Calendar.getInstance();
        refreshTokenCal.setTime(new Date());
        refreshTokenCal.add(Calendar.MONTH, 1);

        // RefreshToken : 1달 후 만료
        Date refreshTokenExpiresIn = refreshTokenCal.getTime();
        return Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}

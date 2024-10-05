package gateway.security;

import gateway.exception.JwtAccessDeniedException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import gateway.service.CustomMemberDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    private final CustomMemberDetailsService customUserDetailsService;

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, CustomMemberDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        key = Keys.hmacShaKeyFor(keyBytes);
    }


    //Jwt Token -> 권한 확인
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        // 권한 정보 없을 시 시행
        if (claims.get("auth") == null) {
            throw new JwtAccessDeniedException("권한 정보가 없는 토큰입니다.");
        }

        UserDetails principal = customUserDetailsService.loadUserByUsername(claims.getSubject());
        log.info("principalThis={}",principal);
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    // token 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
            throw new JwtAccessDeniedException();
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
            // 만료 토큰 재발급 필요
            throw new JwtAccessDeniedException("Access Token이 만료되었습니다. 토큰을 갱신해 주세요.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
            throw new JwtAccessDeniedException("올바르지 않은 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
            throw new JwtAccessDeniedException("정상적이지 않은 접근입니다.");
        }
    }

    // 넘어온 accessToken의 정보 조회
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

}

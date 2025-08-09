package gateway.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.server.WebFilter;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) // REST API이기 때문에 Basic Auth 비활성화
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) // 폼 기반 로그인 비활성화
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // CSRF 비활성화 (6.1부터 권장되는 방식)
                .cors(ServerHttpSecurity.CorsSpec::disable) // CORS 비활성화
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // 상태 비저장
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(JwtSecurityUtils.ALL_METHOD_PERMIT_ALL_PATHS).permitAll() // 공개 경로 설정
                        .pathMatchers(HttpMethod.POST, JwtSecurityUtils.POST_METHOD_PERMIT_ALL_PATHS).permitAll() // 공개 경로 설정
                        .anyExchange().authenticated() // 이외의 모든 요청 인증 필요
                )
                .addFilterAt(jwtAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .build();
    }

    private WebFilter jwtAuthenticationWebFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }
}

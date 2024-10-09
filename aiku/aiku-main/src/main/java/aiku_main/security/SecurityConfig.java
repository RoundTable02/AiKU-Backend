package aiku_main.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic(HttpBasicConfigurer::disable) // REST API이기 때문에 basic auth와 csrf 보안 사용x
                .cors(CorsConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 권한 설정 시작
                .authorizeHttpRequests(authorize ->
                        authorize
//                                .requestMatchers("/login/sign-in").permitAll() // 모든 사용자 허용
//                                .requestMatchers("/login/refresh").permitAll() // 모든 사용자 허용
//                                .requestMatchers(HttpMethod.POST, "/users").permitAll() // 모든 사용자 허용
//                                .requestMatchers("/users/nickname").permitAll() // 모든 사용자 허용
//                                .anyRequest().authenticated() // 이외 모든 요청 인증 필요
                                .anyRequest().permitAll() // 이외 모든 요청 허용
                        )
                .build();
    }
}

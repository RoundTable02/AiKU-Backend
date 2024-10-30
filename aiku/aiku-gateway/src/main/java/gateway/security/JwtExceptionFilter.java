package gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import gateway.exception.JwtAccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtExceptionFilter implements WebFilter {

    /*
     * 인증 오류가 아닌, JWT 관련 오류는 이 필터에서 따로 잡아낸다.
     * 이를 통해 JWT 만료 에러와 인증 에러를 따로 잡아낼 수 있다.
     */
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        try {
            return chain.filter(exchange)
                    .onErrorResume(JwtException.class, ex -> setErrorResponse(exchange, ex));
        } catch (JwtException ex) {
            return setErrorResponse(exchange, ex);
        }
    }

    private Mono<Void> setErrorResponse(ServerWebExchange exchange, Throwable ex) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // JWT 예외에 대한 커스텀 응답 객체 생성
        String errorResponse = "";
        try {
            errorResponse = objectMapper.writeValueAsString(new JwtAccessDeniedException());
        } catch (Exception e) {
            errorResponse = "{\"error\": \"Access Denied\"}";
        }

        byte[] bytes = errorResponse.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}

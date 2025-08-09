package gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import gateway.exception.BaseErrorResponse;
import gateway.exception.JwtAccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements ServerAccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException deniedException) {
        return sendErrorResponse(exchange);
    }

    // 인가 실패 시 처리
    private Mono<Void> sendErrorResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        BaseErrorResponse errorResponse = new BaseErrorResponse(50000, "권힌이 없습니다.");

        String responseBody;

        try {
            responseBody = objectMapper.writeValueAsString(errorResponse);
        } catch (Exception e) {
            responseBody = "{\"code\": 500, \"message\": \"internal server error\", \"requestId\": null}";
        }

        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}

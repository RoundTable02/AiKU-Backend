package gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(-2)
public class GlobalWebExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof JwtAccessDeniedException) {
            return handleJwtAccessDeniedException(exchange, (JwtAccessDeniedException) ex);
        }

        return handleDefaultException(exchange, ex);
    }

    private Mono<Void> handleJwtAccessDeniedException(ServerWebExchange exchange, JwtAccessDeniedException ex) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        BaseErrorResponse errorResponse = new BaseErrorResponse(40300, ex.getMessage());

        log.error("GlobalWebExceptionHandler.handle_JwtAccessDeniedException <{}>", ex.getMessage(), ex);

        return getErrorMessage(exchange, errorResponse);
    }

    private Mono<Void> handleDefaultException(ServerWebExchange exchange, Throwable ex) {
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        BaseErrorResponse errorResponse = new BaseErrorResponse(50000, ex.getMessage());

        log.error("GlobalWebExceptionHandler.handle_DefaultException <{}>", ex.getMessage(), ex);

        return getErrorMessage(exchange, errorResponse);
    }

    private Mono<Void> getErrorMessage(ServerWebExchange exchange, BaseErrorResponse errorResponse) {
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

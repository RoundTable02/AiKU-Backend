package aiku_main.oauth.error;

import aiku_main.exception.InvalidIdTokenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import feign.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;

@Getter
@NoArgsConstructor
@JsonNaming(SnakeCaseStrategy.class)
public class KakaoKauthErrorResponse {
    private String error;
    private String errorCode;
    private String errorDescription;

    public static KakaoKauthErrorResponse from(Response response) {
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(bodyIs, KakaoKauthErrorResponse.class);
        } catch (IOException e) {
            throw new InvalidIdTokenException();
        }
    }
}
package aiku_main.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ObjectMapperUtil {

    private final ObjectMapper objectMapper;

    public <T> T parseJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("ObjectMapper를 통한 역직렬화가 불가능합니다.", e);
        }
    }

    public String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("ObjectMapper를 통한 직렬화가 불가능합니다.", e);
        }
    }
}

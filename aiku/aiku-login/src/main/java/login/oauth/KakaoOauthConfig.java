package login.oauth;

import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import login.oauth.error.KauthErrorDecoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(KauthErrorDecoder.class)
public class KakaoOauthConfig {

    @Bean
    @ConditionalOnMissingBean(value = ErrorDecoder.class)
    public KauthErrorDecoder commonFeignErrorDecoder() {
        return new KauthErrorDecoder();
    }

    @Bean
    Encoder formEncoder() {
        return new feign.form.FormEncoder();
    }
}

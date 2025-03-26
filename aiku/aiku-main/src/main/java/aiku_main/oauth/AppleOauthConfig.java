package aiku_main.oauth;

import aiku_main.oauth.error.AppleErrorDecoder;
import aiku_main.oauth.error.KauthErrorDecoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(AppleErrorDecoder.class)
public class AppleOauthConfig {

    @Bean
    @ConditionalOnMissingBean(value = ErrorDecoder.class)
    public AppleErrorDecoder commonFeignErrorDecoder() {
        return new AppleErrorDecoder();
    }

    @Bean
    Encoder formEncoder() {
        return new feign.form.FormEncoder();
    }
}

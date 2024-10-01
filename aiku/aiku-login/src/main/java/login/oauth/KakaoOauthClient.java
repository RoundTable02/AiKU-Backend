package login.oauth;

import login.oauth.dto.OIDCPublicKeysResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "KakaoAuthClient",
        url = "https://kauth.kakao.com",
        configuration = KakaoOauthConfig.class)
public interface KakaoOauthClient {
        @Cacheable(cacheNames = "KakaoOICD", cacheManager = "oidcCacheManager")
        @GetMapping("/.well-known/jwks.json")
        OIDCPublicKeysResponse getKakaoOIDCOpenKeys();
}

package aiku_main.oauth;

import aiku_main.oauth.dto.OIDCPublicKeysResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "AppleOAuthClient",
        url = "https://appleid.apple.com",
        configuration = AppleOauthConfig.class)
public interface AppleOauthClient {
        @Cacheable(cacheNames = "AppleOICD", cacheManager = "oidcCacheManager")
        @GetMapping("/auth/keys")
        OIDCPublicKeysResponse getAppleOIDCOpenKeys();
}
package aiku_main.oauth;

import aiku_main.oauth.dto.OIDCDecodePayload;
import aiku_main.oauth.dto.OIDCPublicKeysResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AppleOauthHelper {
    @Value("${oidc.apple.base_url}")
    private String baseUrl;

    @Value("${oidc.apple.app_id}")
    private String appId;

    private final AppleOauthClient appleOauthClient;
    private final OauthOIDCHelper oauthOIDCHelper;

    public OIDCDecodePayload getOIDCDecodePayload(String token) {
        // 공개키 목록을 조회한다. 캐싱이 되어있다.
        OIDCPublicKeysResponse oidcPublicKeysResponse = appleOauthClient.getAppleOIDCOpenKeys();

        return oauthOIDCHelper.getPayloadFromIdToken(
                //idToken
                token,
                // iss 와 대응되는 값
                baseUrl,
                // aud 와 대응되는값
                appId,
                // 공개키 목록
                oidcPublicKeysResponse);
    }

    public OauthInfo getOauthInfoByIdToken(String idToken) {
        OIDCDecodePayload oidcDecodePayload = getOIDCDecodePayload(idToken);
        return new OauthInfo(oidcDecodePayload.getSub());
    }
}

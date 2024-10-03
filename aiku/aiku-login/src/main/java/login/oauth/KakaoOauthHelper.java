package login.oauth;

import login.oauth.dto.OIDCDecodePayload;
import login.oauth.dto.OIDCPublicKeysResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KakaoOauthHelper {
    @Value("${kakao.base_url}")
    private String baseUrl;

    @Value("${kakao.app_id}")
    private String appId;

    private final KakaoOauthClient kakaoOauthClient;
    private final OauthOIDCHelper oauthOIDCHelper;

    public OIDCDecodePayload getOIDCDecodePayload(String token) {
        // 공개키 목록을 조회한다. 캐싱이 되어있다.
        OIDCPublicKeysResponse oidcPublicKeysResponse = kakaoOauthClient.getKakaoOIDCOpenKeys();

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

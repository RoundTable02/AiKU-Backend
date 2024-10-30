package aiku_main.oauth;

import aiku_main.exception.InvalidIdTokenException;
import aiku_main.oauth.dto.OIDCDecodePayload;
import aiku_main.oauth.dto.OIDCPublicKeyDto;
import aiku_main.oauth.dto.OIDCPublicKeysResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OauthOIDCHelper {
    private final JwtOIDCProvider jwtOIDCProvider;

    private String getKidFromUnsignedIdToken(String token, String iss, String aud) {
        return jwtOIDCProvider.getKidFromUnsignedTokenHeader(token, iss, aud);
    }

    public OIDCDecodePayload getPayloadFromIdToken(
            String token, String iss, String aud, OIDCPublicKeysResponse oidcPublicKeysResponse) {
        String kid = getKidFromUnsignedIdToken(token, iss, aud);

        OIDCPublicKeyDto oidcPublicKeyDto =
                oidcPublicKeysResponse.getKeys().stream()
                        .filter(o -> o.getKid().equals(kid))
                        .findFirst()
                        .orElseThrow(() -> new InvalidIdTokenException());

        return jwtOIDCProvider.getOIDCTokenBody(
                token, oidcPublicKeyDto.getN(), oidcPublicKeyDto.getE());
    }
}

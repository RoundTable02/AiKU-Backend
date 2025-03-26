package aiku_main.oauth.error;

import aiku_main.exception.InvalidIdTokenException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class KauthErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        OauthErrorResponse body = OauthErrorResponse.from(response);

        try {
            KakaoOauthErrorCode kakaoKauthErrorCode =
                    KakaoOauthErrorCode.valueOf(body.getErrorCode());
            OauthErrorReason errorReason = kakaoKauthErrorCode.getErrorReason();
            throw new InvalidIdTokenException(errorReason.getReason());
        } catch (IllegalArgumentException e) {
            KakaoOauthErrorCode koeInvalidRequest = KakaoOauthErrorCode.KOE_INVALID_REQUEST;
            OauthErrorReason errorReason = koeInvalidRequest.getErrorReason();
            throw new InvalidIdTokenException(errorReason.getReason());
        }
    }
}

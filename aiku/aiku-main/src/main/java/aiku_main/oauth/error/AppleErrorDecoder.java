package aiku_main.oauth.error;

import aiku_main.exception.InvalidIdTokenException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class AppleErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        OauthErrorResponse body = OauthErrorResponse.from(response);

        try {
            AppleOauthErrorCode appleOauthErrorCode =
                    AppleOauthErrorCode.valueOf(body.getErrorCode());
            OauthErrorReason errorReason = appleOauthErrorCode.getErrorReason();
            throw new InvalidIdTokenException(errorReason.getReason());
        } catch (IllegalArgumentException e) {
            AppleOauthErrorCode koeInvalidRequest = AppleOauthErrorCode.KOE_INVALID_REQUEST;
            OauthErrorReason errorReason = koeInvalidRequest.getErrorReason();
            throw new InvalidIdTokenException(errorReason.getReason());
        }
    }
}

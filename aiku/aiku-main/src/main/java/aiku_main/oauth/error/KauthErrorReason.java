package aiku_main.oauth.error;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KauthErrorReason {
    private final Integer status;
    private final String code;
    private final String reason;
}

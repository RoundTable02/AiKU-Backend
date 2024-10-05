package aiku_main.oauth;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OauthInfo {
    private String provider = "KAKAO";

    private String oid;

    public OauthInfo(String oid) {
        this.oid = oid;
    }
}

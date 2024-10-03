package login.oauth;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

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

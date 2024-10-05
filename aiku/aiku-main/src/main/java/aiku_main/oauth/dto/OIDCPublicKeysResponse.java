package aiku_main.oauth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OIDCPublicKeysResponse {
    List<OIDCPublicKeyDto> keys;
}
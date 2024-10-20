package common.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlarmMemberInfo {

    private Long userId;
    private String nickname;
    private String firebaseToken;
}

package common.kafka_message.alarm;

import common.domain.member.Member;
import common.domain.member.MemberProfile;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class AlarmMemberInfo {

    private Long memberId;
    private String nickname;
    private MemberProfile memberProfile;
    private String firebaseToken;

    public AlarmMemberInfo(Member member) {
        this.memberId = member.getId();
        this.nickname = member.getNickname();
        this.memberProfile = member.getProfile();
        this.firebaseToken = member.getFirebaseToken();
    }

    public String getAlarmMemberInfoJsonString() {
        String json = "{" +
                "memberId" + ":" + memberId + "," +
                "nickname" + ":" + nickname + "," +
                "memberProfile" + ":" + memberProfile.getMemberProfileJson() +
                "}";

        return json;
    }
}

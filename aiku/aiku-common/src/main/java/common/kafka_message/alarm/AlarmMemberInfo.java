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
                "memberProfile" + ":" + getMemberProfileJson() +
                "}";

        return json;
    }

    private String getMemberProfileJson() {
        String json = "{" +
                "profileType" + ":" + memberProfile.getProfileType().name() + "," +
                "profileImg" + ":" + memberProfile.getProfileImg() + "," +
                "profileCharacter" + ":" + memberProfile.getProfileCharacter().name() + "," +
                "profileBackground" + ":" + memberProfile.getProfileBackground().name() +
                "}";

        return json;
    }
}

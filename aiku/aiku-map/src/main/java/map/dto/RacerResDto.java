package map.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RacerResDto {
    private Long memberId;
    private String nickname;
    private MemberProfileDto memberProfile;
}

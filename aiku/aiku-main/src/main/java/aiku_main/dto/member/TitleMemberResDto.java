package aiku_main.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TitleMemberResDto {
    private Long titleMemberId;
    private String titleName;
    private String titleDescription;
    private String titleImg;
}

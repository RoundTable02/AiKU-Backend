package common.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Title {
    C01("타이틀 제목", "타이틀 설명", "타이틀 이미지"),
    C02("타이틀 제목", "타이틀 설명", "타이틀 이미지");

    private String titleName;
    private String titleDescription;
    private String titleImg;
}

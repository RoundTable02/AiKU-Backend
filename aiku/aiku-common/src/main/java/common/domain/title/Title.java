package common.domain.title;

import common.domain.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Title extends BaseTime {

    @Column(name = "titleId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titleName;
    private String titleDescription;
    private String titleImg;

    @Builder
    public Title(String titleName, String titleDescription, String titleImg) {
        this.titleName = titleName;
        this.titleDescription = titleDescription;
        this.titleImg = titleImg;
    }
}

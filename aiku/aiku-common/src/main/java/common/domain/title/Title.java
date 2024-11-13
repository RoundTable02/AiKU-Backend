package common.domain.title;

import common.domain.BaseTime;
import common.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(value = EnumType.STRING)
    private TitleCode titleCode;

    @OneToMany(mappedBy = "title", cascade = CascadeType.ALL)
    private List<TitleMember> titleMembers = new ArrayList<>();

    public static Title create(String titleName, String titleDescription, String titleImg, TitleCode titleCode) {
        Title title = new Title();
        title.titleName = titleName;
        title.titleDescription = titleDescription;
        title.titleImg = titleImg;
        title.titleCode = titleCode;

        return title;
    }

    public void giveTitleToMember(Member member) {
        TitleMember titleMember = new TitleMember(member, this);
        this.titleMembers.add(titleMember);
    }
}

package common.domain.term;

import common.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Term extends BaseTime {

    @Column(name = "termId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private TermTitle termTitle;

    private String content;

    @Enumerated(value = EnumType.STRING)
    private AgreedType agreedType;

    private int version;

    public static Term create(TermTitle termTitle, String content, AgreedType agreedType, int version) {
        Term term = new Term();
        term.termTitle = termTitle;
        term.content = content;
        term.agreedType = agreedType;
        term.version = version;

        return term;
    }
}

package common.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Term extends BaseTime{

    @Column(name = "termId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private TermTitle title;
    private String content;

    @Enumerated(value = EnumType.STRING)
    private AgreedType agreed_type;

    @Enumerated(value = EnumType.STRING)
    private TermStatus termStatus;
    private int version;
}

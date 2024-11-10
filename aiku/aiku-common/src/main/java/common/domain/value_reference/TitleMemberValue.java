package common.domain.value_reference;

import common.domain.schedule.ScheduleMember;
import common.domain.title.TitleMember;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class TitleMemberValue {

    @Column(name = "titleMemberId")
    private Long id;

    public TitleMemberValue(TitleMember titleMember) {
        this.id = titleMember.getId();
    }

    public TitleMemberValue(Long id) {
        this.id = id;
    }
}

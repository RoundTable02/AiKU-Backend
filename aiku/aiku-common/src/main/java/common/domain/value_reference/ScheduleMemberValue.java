package common.domain.value_reference;

import common.domain.schedule.ScheduleMember;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class ScheduleMemberValue {

    @Column(name = "scheduleMemberId")
    private Long id;

    public ScheduleMemberValue(ScheduleMember scheduleMember) {
        this.id = scheduleMember.getId();
    }
}

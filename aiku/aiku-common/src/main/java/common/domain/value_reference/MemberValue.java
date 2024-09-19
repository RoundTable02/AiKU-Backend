package common.domain.value_reference;

import common.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class MemberValue {

    @Column(name = "memberId")
    private Long id;

    public MemberValue(Member member) {
        this.id = member.getId();
    }
}

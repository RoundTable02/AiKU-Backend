package common.domain.value_reference;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class TitleMemberValue {

    @Column(name = "titleMemberId")
    private Long id;
}

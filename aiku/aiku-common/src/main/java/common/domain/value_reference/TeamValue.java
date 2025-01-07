package common.domain.value_reference;

import common.domain.team.Team;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Embeddable
public class TeamValue {

    @Column(name = "teamId")
    private Long id;

    //TODO 도메인으로 생성하는 로직 제거
    public TeamValue(Team team) {
        this.id = team.getId();
    }

    public TeamValue(Long id) {
        this.id = id;
    }
}

package common.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Team extends BaseTime{

    @Column(name = "teamId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String teamName;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    private Team(String teamName) {
        this.teamName = teamName;
    }

    //==생성 메서드==
    public static Team create(String teamName){
        Team team = new Team(teamName);
        team.status = Status.ALIVE;
        return team;
    }
}

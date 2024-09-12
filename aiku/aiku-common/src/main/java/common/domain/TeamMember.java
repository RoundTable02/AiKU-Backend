package common.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TeamMember extends BaseTime{

    @Column(name = "teamMemberId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "teamId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;

    @JoinColumn(name = "memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private boolean isOwner;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    protected TeamMember(Team team, Member member, boolean isOwner) {
        this.team = team;
        this.member = member;
        this.isOwner = isOwner;
        this.status = Status.ALIVE;
    }
}

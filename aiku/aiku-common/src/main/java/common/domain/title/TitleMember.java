package common.domain.title;

import common.domain.BaseTime;
import common.domain.Status;
import common.domain.member.Member;
import common.domain.schedule.Schedule;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TitleMember extends BaseTime {

    @Column(name = "titleMemberId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "memberId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JoinColumn(name = "scheduleId")
    @ManyToOne(fetch = FetchType.LAZY)
    private Title title;

    protected TitleMember(Member member, Title title) {
        this.member = member;
        this.title = title;
    }

    public static TitleMember giveTitleToMember(Member member, Title title) {
        return new TitleMember(member, title);
    }
}

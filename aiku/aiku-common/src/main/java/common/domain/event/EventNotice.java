package common.domain.event;

import common.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class EventNotice extends BaseTime {

    @Column(name = "eventNoticeId")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventImg;
}

package aiku_main.dto.member;

import common.domain.AgreedType;
import common.domain.Term;
import common.domain.TermTitle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TermResDto {
    private TermTitle title;
    private String content;
    private AgreedType agreedType;

    public static TermResDto toDto(Term term) {
        return new TermResDto(term.getTermTitle(), term.getContent(), term.getAgreedType());
    }
}

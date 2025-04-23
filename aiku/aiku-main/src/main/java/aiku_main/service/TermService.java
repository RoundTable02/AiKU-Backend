package aiku_main.service;

import aiku_main.dto.member.TermResDto;
import aiku_main.exception.TermException;
import aiku_main.repository.term.TermRepository;
import common.domain.Term;
import common.domain.TermTitle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static common.response.status.BaseErrorCode.NO_SUCH_TERM;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TermService {

    private final TermRepository termRepository;

    public TermResDto getTerm(TermTitle termTitle) {
        Term term = termRepository.findTopByTermTitleOrderByVersionDesc(termTitle)
                .orElseThrow(() -> new TermException(NO_SUCH_TERM));

        return TermResDto.toDto(term);
    }
}

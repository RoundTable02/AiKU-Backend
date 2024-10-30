package aiku_main.service;

import aiku_main.dto.TermResDto;
import aiku_main.exception.NoSuchTermException;
import aiku_main.repository.TermRepository;
import common.domain.Term;
import common.domain.TermTitle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TermService {
    private final TermRepository termRepository;

    public List<TermResDto> getTermsRes() {
        List<Term> terms = getTerms();

        return terms.stream()
                .map(TermResDto::toDto)
                .collect(Collectors.toList());
    }

    private List<Term> getTerms() {
        Term serviceTerm = termRepository.findTopByTermTitleOrderByVersionDesc(TermTitle.SERVICE)
                .orElseThrow(() -> new NoSuchTermException());
        Term marketingTerm = termRepository.findTopByTermTitleOrderByVersionDesc(TermTitle.MARKETING)
                .orElseThrow(() -> new NoSuchTermException());
        Term personalInfoTerm = termRepository.findTopByTermTitleOrderByVersionDesc(TermTitle.PERSONALINFO)
                .orElseThrow(() -> new NoSuchTermException());
        Term locationTerm = termRepository.findTopByTermTitleOrderByVersionDesc(TermTitle.LOCATION)
                .orElseThrow(() -> new NoSuchTermException());

        return List.of(serviceTerm, marketingTerm, personalInfoTerm, locationTerm);
    }
}

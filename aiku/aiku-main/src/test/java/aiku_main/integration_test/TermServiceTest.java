package aiku_main.integration_test;

import aiku_main.dto.TermResDto;
import aiku_main.exception.NoSuchTermException;
import aiku_main.repository.TermRepository;
import aiku_main.service.TermService;
import common.domain.AgreedType;
import common.domain.Term;
import common.domain.TermTitle;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class TermServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    TermRepository termRepository;

    @Autowired
    TermService termService;

    @Test
    void 약관_모두_존재_정상() {
        // given
        Term sTerm = Term.create(TermTitle.SERVICE, "서비스", AgreedType.MANDATORY, 1);
        Term mTerm = Term.create(TermTitle.MARKETING, "마케팅", AgreedType.OPTIONAL, 1);
        Term lTerm = Term.create(TermTitle.LOCATION, "위치", AgreedType.MANDATORY, 1);
        Term pTerm = Term.create(TermTitle.PERSONALINFO, "개인정보", AgreedType.MANDATORY, 1);

        List<Term> termList = List.of(sTerm, lTerm, pTerm, mTerm);

        termRepository.saveAll(termList);

        // when
        List<TermResDto> termsRes = termService.getTermsRes();

        // then
        TermResDto sTermRes = termsRes.stream()
                .filter(t -> t.getTitle().equals(TermTitle.SERVICE))
                .findFirst()
                .orElseThrow(() -> new NullPointerException());

        assertThat(sTermRes.getAgreedType()).isEqualTo(AgreedType.MANDATORY);
        assertThat(sTermRes.getContent()).isEqualTo("서비스");

        TermResDto mTermRes = termsRes.stream()
                .filter(t -> t.getTitle().equals(TermTitle.MARKETING))
                .findFirst()
                .orElseThrow(() -> new NullPointerException());

        assertThat(mTermRes.getAgreedType()).isEqualTo(AgreedType.OPTIONAL);
        assertThat(mTermRes.getContent()).isEqualTo("마케팅");
    }

    @Test
    void 약관_최신화_정상() {
        // given
        Term sTerm = Term.create(TermTitle.SERVICE, "서비스", AgreedType.MANDATORY, 1);
        Term mTerm = Term.create(TermTitle.MARKETING, "마케팅", AgreedType.OPTIONAL, 1);
        Term mTerm2 = Term.create(TermTitle.MARKETING, "마케팅2", AgreedType.OPTIONAL, 2);
        Term lTerm = Term.create(TermTitle.LOCATION, "위치", AgreedType.MANDATORY, 1);
        Term pTerm = Term.create(TermTitle.PERSONALINFO, "개인정보", AgreedType.MANDATORY, 1);

        List<Term> termList = List.of(sTerm, lTerm, pTerm, mTerm, mTerm2);

        termRepository.saveAll(termList);

        // when
        List<TermResDto> termsRes = termService.getTermsRes();

        // then
        TermResDto mTermRes = termsRes.stream()
                .filter(t -> t.getTitle().equals(TermTitle.MARKETING))
                .findFirst()
                .orElseThrow(() -> new NullPointerException());

        assertThat(mTermRes.getAgreedType()).isEqualTo(AgreedType.OPTIONAL);
        assertThat(mTermRes.getContent()).isEqualTo("마케팅2");
    }

    @Test
    void 약관_일부_부재_예외() {
        // given
        Term sTerm = Term.create(TermTitle.SERVICE, "서비스", AgreedType.MANDATORY, 1);
        Term lTerm = Term.create(TermTitle.LOCATION, "위치", AgreedType.MANDATORY, 1);
        Term pTerm = Term.create(TermTitle.PERSONALINFO, "개인정보", AgreedType.MANDATORY, 1);

        List<Term> termList = List.of(sTerm, lTerm, pTerm);

        termRepository.saveAll(termList);

        // when & then
        Assertions.assertThrows(NoSuchTermException.class, () -> {
            termService.getTermsRes();
        });
    }
}
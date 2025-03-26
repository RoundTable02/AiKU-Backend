package aiku_main.controller;

import aiku_main.dto.DataResDto;
import aiku_main.dto.member.TermResDto;
import aiku_main.service.TermService;
import common.domain.TermTitle;
import common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/term/{termTitle}")
@RequiredArgsConstructor
@RestController
public class TermController {

    private final TermService termService;

    @GetMapping
    public BaseResponse<DataResDto> getTerms(@PathVariable String termTitle) {
        TermResDto termsRes = termService.getTerm(TermTitle.valueOf(termTitle));

        return new BaseResponse(termsRes);
    }
}

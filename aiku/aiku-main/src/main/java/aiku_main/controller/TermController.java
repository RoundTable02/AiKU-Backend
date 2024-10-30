package aiku_main.controller;

import aiku_main.dto.DataResDto;
import aiku_main.dto.TermResDto;
import aiku_main.service.TermService;
import common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/term")
@RequiredArgsConstructor
@RestController
public class TermController {

    private final TermService termService;

    @GetMapping
    public BaseResponse<DataResDto> getTerms() {
        List<TermResDto> termsRes = termService.getTermsRes();

        DataResDto<List<TermResDto>> listDataResDto = new DataResDto<>(1, termsRes);
        return new BaseResponse(listDataResDto);
    }
}

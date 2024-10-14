package aiku_main.controller;

import aiku_main.dto.InquiryDto;
import aiku_main.service.EmailService;
import common.domain.member.Member;
import common.response.BaseResponse;
import common.response.BaseResultDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/mail")
@RequiredArgsConstructor
@RestController
public class EmailSendController {
    private final EmailService emailService;

    @PostMapping(path = "/request")
    public BaseResponse<BaseResultDto> submitContactRequest(@RequestBody Member member, @ModelAttribute @Valid InquiryDto inquiryDto) {
        Long memberId = emailService.submitContactRequest(member, inquiryDto);

        return BaseResponse.getSimpleRes(memberId);
    }

}

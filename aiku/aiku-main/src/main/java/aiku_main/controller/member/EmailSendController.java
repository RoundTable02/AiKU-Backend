package aiku_main.controller.member;

import aiku_main.dto.InquiryDto;
import aiku_main.service.member.EmailService;
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
    public BaseResponse<BaseResultDto> submitContactRequest(@RequestHeader(name = "Access-Member-Id") Long accessMemberId,
                                                            @ModelAttribute @Valid InquiryDto inquiryDto) {
        Long memberId = emailService.submitContactRequest(accessMemberId, inquiryDto);

        return BaseResponse.getSimpleRes(memberId);
    }

}

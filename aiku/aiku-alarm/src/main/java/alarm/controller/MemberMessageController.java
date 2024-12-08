package alarm.controller;

import alarm.controller.dto.DataResDto;
import alarm.controller.dto.MemberMessageDto;
import alarm.service.MemberMessageService;
import common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/alarm")
@RequiredArgsConstructor
@Controller
public class MemberMessageController {

    private final MemberMessageService memberMessageService;

    @GetMapping
    public BaseResponse getMemberMessages(@RequestHeader(name = "Access-Member-Id") Long memberId,
                                          @RequestParam(defaultValue = "1") int page) {
        DataResDto<List<MemberMessageDto>> message = memberMessageService.getMemberMessageByMemberId(memberId, page);

        return new BaseResponse(message);
    }
}

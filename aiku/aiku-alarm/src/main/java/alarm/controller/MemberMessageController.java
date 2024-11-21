package alarm.controller;

import alarm.controller.dto.MemberMessageDto;
import alarm.service.MemberMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/alarm")
@RequiredArgsConstructor
@Controller
public class MemberMessageController {

    private final MemberMessageService memberMessageService;

    @GetMapping
    public List<MemberMessageDto> getMemberMessages(@RequestHeader(name = "Access-Member-Id") Long memberId) {
        return memberMessageService.getMemberMessageByMemberId(memberId);
    }
}

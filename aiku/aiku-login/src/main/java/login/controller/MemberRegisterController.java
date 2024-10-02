package login.controller;

import common.response.BaseResponse;
import common.response.BaseResultDto;
import common.response.status.BaseCode;
import login.dto.*;
import login.service.MemberRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static common.response.status.BaseCode.POST;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class MemberRegisterController {
    private final MemberRegisterService memberRegisterService;
    @PostMapping
    public BaseResponse<BaseResultDto> register(@RequestBody MemberRegisterDto memberRegisterDto){
        Long addId = memberRegisterService.register(memberRegisterDto);

        return BaseResponse.getSimpleRes(addId, POST);
    }

    @GetMapping("/nickname")
    public BaseResponse<NicknameExistResDto> checkNickname(@RequestBody NicknameExistDto nicknameExistDto) {
        NicknameExistResDto checkNickname = memberRegisterService.checkNickname(nicknameExistDto);

        return new BaseResponse<>(checkNickname);
    }
}

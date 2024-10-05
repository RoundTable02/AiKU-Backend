package aiku_main.controller;

import aiku_main.dto.MemberRegisterDto;
import aiku_main.dto.NicknameExistDto;
import aiku_main.dto.NicknameExistResDto;
import aiku_main.service.MemberRegisterService;
import common.response.BaseResponse;
import common.response.BaseResultDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@RestController
public class MemberRegisterController {
    private final MemberRegisterService memberRegisterService;
    @PostMapping
    public BaseResponse<BaseResultDto> register(@ModelAttribute @Valid MemberRegisterDto memberRegisterDto){
        Long addId = memberRegisterService.register(memberRegisterDto);

        return BaseResponse.getSimpleRes(addId);
    }

    @GetMapping("/nickname")
    public BaseResponse<NicknameExistResDto> checkNickname(@RequestBody NicknameExistDto nicknameExistDto) {
        NicknameExistResDto checkNickname = memberRegisterService.checkNickname(nicknameExistDto);

        return new BaseResponse<>(checkNickname);
    }
}

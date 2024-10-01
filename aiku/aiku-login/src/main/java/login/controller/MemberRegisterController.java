package login.controller;

import common.response.BaseResponse;
import common.response.BaseResultDto;
import common.response.status.BaseCode;
import login.dto.MemberRegisterDto;
import login.dto.SignInDto;
import login.dto.SignInTokenResDto;
import login.service.MemberRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

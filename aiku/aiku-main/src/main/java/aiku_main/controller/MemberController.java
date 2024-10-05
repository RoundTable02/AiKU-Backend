package aiku_main.controller;

import aiku_main.dto.MemberResDto;
import aiku_main.dto.MemberUpdateDto;
import aiku_main.service.MemberService;
import common.domain.member.Member;
import common.response.BaseResponse;
import common.response.BaseResultDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public BaseResponse<MemberResDto> getMemberDetail(
            @RequestBody Member member) {
        MemberResDto memberResDto = memberService.getMemberDetail(member);
        return new BaseResponse<>(memberResDto);
    }

    @PatchMapping
    public BaseResponse<BaseResultDto> updateMember(
            @RequestBody Member member,
            @ModelAttribute @Valid MemberUpdateDto memberUpdateDto) {
        Long memberId = memberService.updateMember(member, memberUpdateDto);

        return BaseResponse.getSimpleRes(memberId);
    }

    @DeleteMapping
    public BaseResponse<BaseResultDto> deleteMember(@RequestBody Member member) {
        Long memberId = memberService.deleteMember(member);

        return BaseResponse.getSimpleRes(memberId);
    }

    @PatchMapping("/titles/{userTitleId}")
    public BaseResponse<BaseResultDto> updateTitle(@RequestBody Member member, @PathVariable Long userTitleId) {
        Long memberId = memberService.updateTitle(member, userTitleId);

        return BaseResponse.getSimpleRes(memberId);
    }





}

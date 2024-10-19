package aiku_main.controller;

import aiku_main.dto.*;
import aiku_main.service.EmailService;
import aiku_main.service.MemberService;
import common.domain.member.Member;
import common.response.BaseResponse;
import common.response.BaseResultDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;

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

    @PostMapping("/logout")
    public BaseResponse<BaseResultDto> logout(@RequestBody Member member) {
        Long memberId = memberService.logout(member);

        return BaseResponse.getSimpleRes(memberId);
    }

    @PatchMapping("/setting/authority")
    public BaseResponse<BaseResultDto> updateAuth(@RequestBody Member member, @RequestBody @Valid AuthorityUpdateDto authorityUpdateDto) {
        Long memberId = memberService.updateAuth(member, authorityUpdateDto);

        return BaseResponse.getSimpleRes(memberId);
    }

    @GetMapping("/setting/authority")
    public BaseResponse<AuthorityResDto> getAuthDetail(@RequestBody Member member) {
        AuthorityResDto authorityResDto = memberService.getAuthDetail(member);

        return new BaseResponse<>(authorityResDto);
    }

    @GetMapping("/titles")
    public BaseResponse<DataResDto> getMemberTitles(@RequestBody Member member) {
        DataResDto<List<TitleMemberResDto>> resDto = memberService.getMemberTitles(member);

        return new BaseResponse<>(resDto);
    }
}

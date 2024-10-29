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

    @GetMapping
    public BaseResponse<MemberResDto> getMemberDetail(@RequestHeader(name = "Access-Member-Id") Long accessMemberId) {
        MemberResDto memberResDto = memberService.getMemberDetail(accessMemberId);
        return new BaseResponse<>(memberResDto);
    }

    @PatchMapping
    public BaseResponse<BaseResultDto> updateMember(
            @RequestHeader(name = "Access-Member-Id") Long accessMemberId,
            @ModelAttribute @Valid MemberUpdateDto memberUpdateDto) {
        Long memberId = memberService.updateMember(accessMemberId, memberUpdateDto);

        return BaseResponse.getSimpleRes(memberId);
    }

    @DeleteMapping
    public BaseResponse<BaseResultDto> deleteMember(@RequestHeader(name = "Access-Member-Id") Long accessMemberId) {
        Long memberId = memberService.deleteMember(accessMemberId);

        return BaseResponse.getSimpleRes(memberId);
    }

    @PatchMapping("/titles/{userTitleId}")
    public BaseResponse<BaseResultDto> updateTitle(@RequestHeader(name = "Access-Member-Id") Long accessMemberId,
                                                   @PathVariable Long userTitleId) {
        Long memberId = memberService.updateTitle(accessMemberId, userTitleId);

        return BaseResponse.getSimpleRes(memberId);
    }

    @PostMapping("/logout")
    public BaseResponse<BaseResultDto> logout(@RequestHeader(name = "Access-Member-Id") Long accessMemberId) {
        Long memberId = memberService.logout(accessMemberId);

        return BaseResponse.getSimpleRes(memberId);
    }

    @PatchMapping("/setting/authority")
    public BaseResponse<BaseResultDto> updateAuth(@RequestHeader(name = "Access-Member-Id") Long accessMemberId,
                                                  @RequestBody @Valid AuthorityUpdateDto authorityUpdateDto) {
        Long memberId = memberService.updateAuth(accessMemberId, authorityUpdateDto);

        return BaseResponse.getSimpleRes(memberId);
    }

    @GetMapping("/setting/authority")
    public BaseResponse<AuthorityResDto> getAuthDetail(@RequestHeader(name = "Access-Member-Id") Long accessMemberId) {
        AuthorityResDto authorityResDto = memberService.getAuthDetail(accessMemberId);

        return new BaseResponse<>(authorityResDto);
    }

    @GetMapping("/titles")
    public BaseResponse<DataResDto> getMemberTitles(@RequestHeader(name = "Access-Member-Id") Long accessMemberId) {
        DataResDto<List<TitleMemberResDto>> resDto = memberService.getMemberTitles(accessMemberId);

        return new BaseResponse<>(resDto);
    }
}

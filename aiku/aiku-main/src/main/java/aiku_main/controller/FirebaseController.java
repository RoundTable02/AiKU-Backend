package aiku_main.controller;

import aiku_main.dto.*;
import aiku_main.service.member.MemberFirebaseService;
import common.response.BaseResponse;
import common.response.BaseResultDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users/alarm/token")
@RequiredArgsConstructor
@RestController
public class FirebaseController {

    private final MemberFirebaseService memberFirebaseService;

    @PostMapping
    public BaseResponse<BaseResultDto> saveToken(@RequestHeader(name = "Access-Member-Id") Long accessMemberId,
                                                 @RequestBody @Valid FirebaseTokenDto firebaseTokenDto) {
        Long memberId = memberFirebaseService.saveToken(accessMemberId, firebaseTokenDto);

        return BaseResponse.getSimpleRes(memberId);
    }

}

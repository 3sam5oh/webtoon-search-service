package com.samsamohoh.webtoonsearch.adapter.web.member;

import com.samsamohoh.webtoonsearch.application.port.in.member.RegisterMemberCommand;
import com.samsamohoh.webtoonsearch.application.port.in.member.RegisterMemberUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/logins")
@RequiredArgsConstructor
public class OAuthController {

    private final RegisterMemberUseCase registerMemberUseCase;

    @PostMapping("/naver")
    public ResponseEntity<String> registerMember(@RequestBody OAuthResponse oAuthResponse) {
        RegisterMemberCommand command = new RegisterMemberCommand(
                oAuthResponse.getEmail(),
                oAuthResponse.getGender(),
                oAuthResponse.getNaverId(),
                oAuthResponse.getAge(),
                oAuthResponse.getNickname()
        );
        registerMemberUseCase.registerMember(command);
        return ResponseEntity.ok("Member registered successfully");
    }
}

package com.samsamohoh.webtoonsearch.adapter.web.member;

import com.samsamohoh.webtoonsearch.application.port.in.member.RegisterMemberCommand;
import com.samsamohoh.webtoonsearch.application.port.in.member.RegisterMemberUseCase;
import lombok.RequiredArgsConstructor;
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
    public void registerUser(@RequestBody OAuthResponse response) {
        RegisterMemberCommand command = new RegisterMemberCommand(
                response.getEmail(),
                response.getGender(),
                response.getId(),
                response.getAge(),
                response.getNickname()
        );
        registerMemberUseCase.registerMember(command);
    }
}

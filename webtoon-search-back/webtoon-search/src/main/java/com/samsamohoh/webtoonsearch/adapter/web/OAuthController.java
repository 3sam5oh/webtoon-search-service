package com.samsamohoh.webtoonsearch.adapter.web;

import com.samsamohoh.webtoonsearch.application.port.in.RegisterUserCommand;
import com.samsamohoh.webtoonsearch.application.port.in.RegisterUserUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthController {

    private final RegisterUserUseCase registerUserUseCase;

    public OAuthController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/api/oauth2/naver")
    public void registerUser(@RequestBody OAuthResponse response) {
        RegisterUserCommand command = new RegisterUserCommand(
                response.getEmail(),
                response.getGender(),
                response.getId(),
                response.getAge(),
                response.getNickname()
        );
        registerUserUseCase.registerUser(command);
    }
}

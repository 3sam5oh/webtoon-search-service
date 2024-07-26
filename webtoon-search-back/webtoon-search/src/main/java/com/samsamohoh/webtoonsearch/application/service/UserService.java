package com.samsamohoh.webtoonsearch.application.service;

import com.samsamohoh.webtoonsearch.application.port.in.RegisterUserCommand;
import com.samsamohoh.webtoonsearch.application.port.in.RegisterUserUseCase;
import com.samsamohoh.webtoonsearch.application.port.out.SaveUserPort;
import com.samsamohoh.webtoonsearch.domain.User;
import org.springframework.stereotype.Service;

@Service
public class UserService implements RegisterUserUseCase {

    private final SaveUserPort saveUserPort;

    public UserService(SaveUserPort saveUserPort) {
        this.saveUserPort = saveUserPort;
    }

    @Override
    public void registerUser(RegisterUserCommand command) {
        User user = new User(command.getEmail(), command.getGender(), command.getNaverId(), command.getAge(), command.getNickname());
        saveUserPort.saveUser(user);
    }
}

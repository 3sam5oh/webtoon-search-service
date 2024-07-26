package com.samsamohoh.webtoonsearch.adapter.persistence;

import com.samsamohoh.webtoonsearch.application.port.out.SaveUserPort;
import com.samsamohoh.webtoonsearch.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceAdapter implements SaveUserPort {

    private final UserRepository userRepository;

    public UserPersistenceAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void saveUser(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(user.getEmail());
        userEntity.setGender(user.getGender());
        userEntity.setNaverId(user.getNaverId());
        userEntity.setAge(user.getAge());
        userEntity.setNickname(user.getNickname());
        userRepository.save(userEntity);
    }
}

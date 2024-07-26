package com.samsamohoh.webtoonsearch.application.port.out;

import com.samsamohoh.webtoonsearch.domain.User;

public interface SaveUserPort {
    void saveUser(User user);
}

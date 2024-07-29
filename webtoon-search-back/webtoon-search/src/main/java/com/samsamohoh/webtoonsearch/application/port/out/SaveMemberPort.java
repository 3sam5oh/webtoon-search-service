package com.samsamohoh.webtoonsearch.application.port.out;

import com.samsamohoh.webtoonsearch.domain.MemberValidation;

public interface SaveMemberPort {
    void saveUser(MemberValidation memberValidation);
}

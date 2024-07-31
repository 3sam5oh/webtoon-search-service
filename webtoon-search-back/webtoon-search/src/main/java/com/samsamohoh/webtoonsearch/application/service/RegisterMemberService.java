package com.samsamohoh.webtoonsearch.application.service;

import com.samsamohoh.webtoonsearch.application.port.in.member.RegisterMemberCommand;
import com.samsamohoh.webtoonsearch.application.port.in.member.RegisterMemberUseCase;
import com.samsamohoh.webtoonsearch.application.port.out.SaveMemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterMemberService implements RegisterMemberUseCase {

    private final SaveMemberPort saveMemberPort;

    @Override
    @Transactional
    public void registerMember(RegisterMemberCommand command) {

        // 여기에 추가적인 비즈니스 로직이나 유효성 검사를 수행할 수 있습니다.
        // 예: 이메일 형식 검사, 나이 범위 검사 등

        // 유효성 검사를 통과하면 회원 정보를 저장
        saveMemberPort.saveMember(command);
    }
}

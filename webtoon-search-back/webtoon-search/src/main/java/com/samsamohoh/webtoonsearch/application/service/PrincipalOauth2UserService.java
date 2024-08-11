package com.samsamohoh.webtoonsearch.application.service;

import com.samsamohoh.webtoonsearch.adapter.web.member.NaverUserInfo;
import com.samsamohoh.webtoonsearch.adapter.web.member.OAuth2UserInfo;
import com.samsamohoh.webtoonsearch.adapter.web.member.PrincipalDetails;
import com.samsamohoh.webtoonsearch.application.port.in.member.MemberResponseDTO;
import com.samsamohoh.webtoonsearch.application.port.out.SaveMemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final SaveMemberPort saveMemberPort;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo;

        if ("naver".equals(provider)) {
            oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
        } else {
            throw new OAuth2AuthenticationException("Provider not supported");
        }

        MemberResponseDTO memberResponseDTO = saveMemberPort.findMemberByProviderId(oAuth2UserInfo.getProviderId());

        if (memberResponseDTO == null) {
            // 신규 회원 등록
            memberResponseDTO = new MemberResponseDTO(
                    oAuth2UserInfo.getProviderId(),
                    oAuth2UserInfo.getEmail(),
                    oAuth2UserInfo.getAge(),
                    oAuth2UserInfo.getGender(),
                    oAuth2UserInfo.getNickname(),
                    provider
            );

            saveMemberPort.saveMember(memberResponseDTO);
        }

        // MemberValidation 대신 필요한 필드를 직접 전달하여 PrincipalDetails 생성
        return new PrincipalDetails(
                memberResponseDTO.getEmail(),
                memberResponseDTO.getGender(),
                memberResponseDTO.getProviderId(),
                memberResponseDTO.getAge(),
                memberResponseDTO.getNickname(),
                memberResponseDTO.getProvider(),
                oAuth2UserInfo
        );
    }
}

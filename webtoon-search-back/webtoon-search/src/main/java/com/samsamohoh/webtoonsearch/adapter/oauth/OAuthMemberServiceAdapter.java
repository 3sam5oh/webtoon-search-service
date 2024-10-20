package com.samsamohoh.webtoonsearch.adapter.oauth;

import com.samsamohoh.webtoonsearch.adapter.oauth.dto.NaverResponse;
import com.samsamohoh.webtoonsearch.application.exception.UserAlreadyExistsException;
import com.samsamohoh.webtoonsearch.application.port.in.member.AuthenticationUseCase;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuthMemberServiceAdapter extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(OAuthMemberServiceAdapter.class);
    private final AuthenticationUseCase authenticationUseCase;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2Response oAuth2Response = extractOAuth2UserInfo(userRequest, oAuth2User);
        String username = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderID();

        OAuthMemberRegisterRequest registerRequest = OAuthMemberRegisterRequest.builder()
                .providerId(oAuth2Response.getProviderID())
                .provider(oAuth2Response.getProvider())
                .email(oAuth2Response.getEmail())
                .username(username)
                .name(((NaverResponse) oAuth2Response).getName())
                .role("ROLE_USER")
                .age(((NaverResponse) oAuth2Response).getAge())
                .gender(((NaverResponse) oAuth2Response).getGender())
                .build();

        try {
            MemberProfileResponse profile = authenticationUseCase.registerNewMember(registerRequest);
            logger.info("New user registered: {}", username);
            return new OAuth2UserAdapter(profile);
        } catch (UserAlreadyExistsException e) {
            logger.info("Existing user logged in: {}", username);
            AuthenticationResponse authResult = authenticationUseCase.authenticateMember(
                    OAuthLoginRequest.builder()
                            .provider(oAuth2Response.getProvider())
                            .username(username)
                            .build()
            );
            return new OAuth2UserAdapter(authResult.getMemberProfile());
        }
    }

    private OAuth2Response extractOAuth2UserInfo(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if ("naver".equals(registrationId)) {
            return new NaverResponse(oAuth2User.getAttributes());
        }
        // Add more providers here as needed
        throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId);
    }
}

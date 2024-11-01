package com.samsamohoh.webtoonsearch.adapter.oauth;

import com.samsamohoh.webtoonsearch.adapter.oauth.dto.NaverResponse;
import com.samsamohoh.webtoonsearch.application.port.in.member.AuthenticationUseCase;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.AuthenticationResponse;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.OAuth2Response;
import com.samsamohoh.webtoonsearch.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

/**
 * OAuth2 인증을 처리하는 외부 어댑터
 * Spring Security OAuth2와 애플리케이션 간의 인터페이스 역할
 */
@Component
@RequiredArgsConstructor
public class OAuthMemberServiceAdapter extends DefaultOAuth2UserService {
    private static final Logger logger = LoggerFactory.getLogger(OAuthMemberServiceAdapter.class);
    private static final String NAVER_PROVIDER = "naver";

    private final AuthenticationUseCase authenticationUseCase;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        validateUserRequest(userRequest);

        try {
            // OAuth2 제공자로부터 사용자 정보 로드
            OAuth2User oAuth2User = super.loadUser(userRequest);
            logger.debug("Successfully loaded OAuth2User from provider");

            // OAuth2 응답을 애플리케이션 형식으로 변환
            OAuth2Response oAuth2Response = extractOAuth2UserInfo(userRequest, oAuth2User);
            logger.debug("Extracted OAuth2 user info: provider={}, providerId={}",
                    oAuth2Response.getProvider(),
                    SecurityUtils.maskValue(oAuth2Response.getProviderID()));

            // 인증 처리를 애플리케이션 서비스에 위임
            AuthenticationResponse authResponse =
                    authenticationUseCase.authenticateOAuth2User(oAuth2Response);

            if (!authResponse.isSuccess()) {
                logger.error("Authentication failed for user: provider={}, providerId={}",
                        oAuth2Response.getProvider(),
                        SecurityUtils.maskValue(oAuth2Response.getProviderID()));
                throw new OAuth2AuthenticationException(
                        new OAuth2Error("authentication_failed"),
                        "Authentication failed"
                );
            }

            logger.info("User authenticated successfully: provider={}, providerId={}",
                    oAuth2Response.getProvider(),
                    SecurityUtils.maskValue(oAuth2Response.getProviderID()));

            // Spring Security용 어댑터로 변환
            return new OAuth2UserAdapter(authResponse.getMemberProfile());

        } catch (OAuth2AuthenticationException ex) {
            logger.error("OAuth2 authentication failed", ex);
            throw ex;
        } catch (Exception ex) {
            logger.error("Unexpected error during OAuth2 authentication", ex);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("authentication_error"),
                    "Authentication failed", ex
            );
        }
    }

    private void validateUserRequest(OAuth2UserRequest userRequest) {
        if (userRequest == null) {
            logger.error("OAuth2UserRequest is null");
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_request"),
                    "UserRequest cannot be null"
            );
        }

        if (userRequest.getClientRegistration() == null) {
            logger.error("ClientRegistration is null");
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_request"),
                    "ClientRegistration cannot be null"
            );
        }

        logger.debug("Validated OAuth2UserRequest: registrationId={}",
                userRequest.getClientRegistration().getRegistrationId());
    }

    private OAuth2Response extractOAuth2UserInfo(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if (oAuth2User.getAttributes() == null || oAuth2User.getAttributes().isEmpty()) {
            logger.error("OAuth2User attributes are null or empty");
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_user_info"),
                    "OAuth2User attributes cannot be null or empty"
            );
        }

        try {
            if (NAVER_PROVIDER.equals(registrationId)) {
                return new NaverResponse(oAuth2User.getAttributes());
            }

            logger.error("Unsupported OAuth2 provider: {}", registrationId);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("unsupported_provider"),
                    String.format("Unsupported OAuth2 provider: %s", registrationId)
            );

        } catch (IllegalArgumentException ex) {
            logger.error("Failed to extract OAuth2 user info", ex);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("invalid_user_info"),
                    "Failed to extract OAuth2 user info: " + ex.getMessage(), ex
            );
        }
    }
}

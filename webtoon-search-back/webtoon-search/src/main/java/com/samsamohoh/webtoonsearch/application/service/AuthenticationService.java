package com.samsamohoh.webtoonsearch.application.service;

import com.samsamohoh.webtoonsearch.application.domain.AuthMemberInfo;
import com.samsamohoh.webtoonsearch.application.port.in.member.AuthenticationUseCase;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.*;
import com.samsamohoh.webtoonsearch.application.port.out.AuthMemberPort;
import com.samsamohoh.webtoonsearch.application.port.out.dto.AuthMemberResponse;
import com.samsamohoh.webtoonsearch.exception.AuthenticationFailedException;
import com.samsamohoh.webtoonsearch.exception.MemberNotFoundException;
import com.samsamohoh.webtoonsearch.exception.RegistrationFailedException;
import com.samsamohoh.webtoonsearch.exception.UserAlreadyExistsException;
import com.samsamohoh.webtoonsearch.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * OAuth2.0 기반 사용자 인증 및 회원 관리 서비스
 * 헥사고날 아키텍처의 유스케이스를 구현하며, MemberInfo 도메인 모델의 비즈니스 규칙을 활용
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationUseCase {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final AuthMemberPort authMemberPort;
    private final AuthMemberDomainMapper authMemberDomainMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * OAuth2.0 사용자 인증 처리
     * 기존 회원과 신규 회원을 구분하여 적절한 인증 프로세스 수행
     */
    @Override
    @Transactional
    public AuthenticationResponse authenticateOAuth2User(OAuth2Response oAuth2Response) {
        validateOAuth2Response(oAuth2Response);
        AuthMemberInfo.Provider provider = validateAndGetProvider(oAuth2Response.getProvider());

        // 회원 존재 여부 먼저 확인
        AuthMemberResponse existingMember = authMemberPort.findByProviderAndProviderId(
                provider.getValue(),
                oAuth2Response.getProviderID()
        );

        // 기존 회원이 없는 경우 신규 등록
        if (existingMember == null) {
            logger.info("User not found, proceeding with registration. Provider: {}, ProviderId: {}",
                    oAuth2Response.getProvider(),
                    SecurityUtils.maskValue(oAuth2Response.getProviderID()));

            OAuthMemberRegisterRequest registerRequest = OAuthMemberRegisterRequest.builder()
                    .providerId(oAuth2Response.getProviderID())
                    .provider(oAuth2Response.getProvider())
                    .email(oAuth2Response.getEmail())
                    .name(oAuth2Response.getName())
                    .role(AuthMemberInfo.Role.USER.getValue())
                    .ageRange(oAuth2Response.getAgeRange())
                    .gender(oAuth2Response.getGender())
                    .status(AuthMemberInfo.MemberStatus.ACTIVE.name())
                    .build();

            MemberProfileResponse profile = registerNewMember(registerRequest);

            logger.info("New member registered successfully: provider={}, email={}, hasAdditionalInfo={}",
                    registerRequest.getProvider(),
                    SecurityUtils.maskEmail(registerRequest.getEmail()),
                    (registerRequest.getAgeRange() != null && registerRequest.getGender() != null)
            );

            return AuthenticationResponse.builder()
                    .success(true)
                    .memberProfile(profile)
                    .build();
        }

        // 기존 회원인 경우 인증 진행
        OAuthLoginRequest loginRequest = OAuthLoginRequest.builder()
                .provider(provider.getValue())
                .providerId(oAuth2Response.getProviderID())
                .build();

        return authenticateMember(loginRequest);
    }


    /**
     * 회원 인증 처리
     * MemberInfo의 상태 검증 로직을 활용하여 인증 처리
     */
    @Override
    @Transactional
    public AuthenticationResponse authenticateMember(OAuthLoginRequest request) {
        validateAuthenticationRequest(request);

        logger.debug("Authenticating member: provider={}, providerId={}",
                request.getProvider(),
                SecurityUtils.maskValue(request.getProviderId()));

        try {
            // Provider enum을 활용한 타입 안전성 확보
            AuthMemberInfo.Provider provider = AuthMemberInfo.Provider.fromString(request.getProvider());
            AuthMemberInfo authMemberInfo = findMemberByProviderAndId(provider, request.getProviderId());

            // MemberInfo의 상태 검증 메소드 활용
            if (!authMemberInfo.isActive()) {
                throw new AuthenticationFailedException("Member is not active");
            }

            // 추천 정보 유무 확인
            if (!authMemberInfo.hasRecommendationInfo()) {
                logger.warn("Member lacks profile information for recommendations");
            }

            AuthenticationResponse response = createSuccessfulAuthResponse(authMemberInfo);
            handleMemberEvents(authMemberInfo);

            logger.info("Member authenticated successfully: {}",
                    SecurityUtils.maskEmail(authMemberInfo.getEmail().getValue()));

            return response;

        } catch (MemberNotFoundException e) {
            logger.warn("Authentication failed: {}", e.getMessage());
            throw new AuthenticationFailedException("Authentication failed", e);
        }
    }

    /**
     * 신규 회원 등록
     * MemberInfo의 팩토리 메서드와 도메인 이벤트를 활용
     */
    @Override
    @Transactional
    public MemberProfileResponse registerNewMember(OAuthMemberRegisterRequest request) {
        validateRegistrationRequest(request);
        checkDuplicateUser(request);

        try {
            // MemberInfo 팩토리 메서드를 통한 도메인 객체 생성
            AuthMemberInfo newMember = AuthMemberInfo.createNewMember(
                    request.getProviderId(),
                    request.getProvider(),
                    request.getEmail(),
                    request.getName(),
                    request.getAgeRange(),
                    request.getGender()
            );

            // 영속성 계층에 저장
            AuthMemberInfo savedMember = authMemberDomainMapper.toDomainModel(
                    authMemberPort.saveMember(
                            authMemberDomainMapper.toPersistenceResponse(newMember)
                    )
            );

            // 추천 서비스 가능 여부 확인
            if (!savedMember.canReceiveRecommendations()) {
                logger.info("New member requires additional information for recommendations");
            }

            handleMemberEvents(savedMember);

            logger.info("New member registered successfully: provider={}, providerId={}",
                    savedMember.getProvider().getValue(),
                    SecurityUtils.maskValue(savedMember.getProviderId()));

            return authMemberDomainMapper.toProfileResponse(savedMember);

        } catch (Exception e) {
            logger.error("Failed to register new member", e);
            throw new RegistrationFailedException("Failed to register new member", e);
        }
    }

    /**
     * 현재 사용자의 프로필 정보 조회
     */
    @Override
    @Transactional
    public MemberProfileResponse getCurrentUserProfile(String providerStr, String providerId) {
        // Provider enum을 활용한 타입 안전성 확보
        AuthMemberInfo.Provider provider = AuthMemberInfo.Provider.fromString(providerStr);

        AuthMemberInfo authMemberInfo = findMemberByProviderAndId(provider, providerId);

        if (!authMemberInfo.hasRecommendationInfo()) {
            logger.info("Member profile lacks recommendation information");
        }

        return authMemberDomainMapper.toProfileResponse(authMemberInfo);
    }

//    /**
//     * 회원 탈퇴 처리
//     * 회원 상태 변경 및 이벤트 발행
//     */
//    @Override
//    @Transactional
//    public void withdrawMember(String memberId) {
//        try {
//            authMemberPort.deleteMember(memberId);
//            logger.info("Member withdrawn successfully: {}", SecurityUtils.maskValue(memberId));
//        } catch (Exception e) {
//            logger.error("Failed to withdraw member: {}", SecurityUtils.maskValue(memberId), e);
//            throw new RuntimeException("Failed to withdraw member", e);
//        }
//    }

    /**
     * 로그아웃 처리
     *
     * @param memberId 회원 ID
     */
    @Override
    @Transactional
    public void logoutMember(String memberId) {
        // TODO: 세션 무효화, 토큰 폐기 등의 로그아웃 처리
        logger.info("Member logged out: {}", SecurityUtils.maskValue(memberId));
    }

    // ************************************************************************************************************** //
    // ************************************************************************************************************** //
    // Private helper methods
    // ************************************************************************************************************** //
    // ************************************************************************************************************** //

    /**
     * 유효하지 않은 Provider 값에 대한 예외 처리
     */
    private AuthMemberInfo.Provider validateAndGetProvider(String providerStr) {
        try {
            return AuthMemberInfo.Provider.fromString(providerStr);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid provider: {}", providerStr);
            throw new IllegalArgumentException("Invalid provider: " + providerStr, e);
        }
    }

    /**
     * OAuth2 응답 유효성 검증
     * 필수 필드 존재 여부 및 Provider 유효성 검증
     */
    private void validateOAuth2Response(OAuth2Response response) {
        List<String> missingFields = new ArrayList<>();

        if (!StringUtils.hasText(response.getProvider())) missingFields.add("provider");
        if (!StringUtils.hasText(response.getProviderID())) missingFields.add("providerId");
        if (!StringUtils.hasText(response.getEmail())) missingFields.add("email");
        if (!StringUtils.hasText(response.getName())) missingFields.add("name");

        if (!missingFields.isEmpty()) {
            throw new IllegalArgumentException(
                    "Required fields missing in OAuth2Response: " + String.join(", ", missingFields)
            );
        }

        validateAndGetProvider(response.getProvider());
    }

    /**
     * 인증 요청 유효성 검증
     * 필수 필드 존재 여부 및 Provider 유효성 검증
     */
    private void validateAuthenticationRequest(OAuthLoginRequest request) {
        List<String> missingFields = new ArrayList<>();

        if (!StringUtils.hasText(request.getProvider())) missingFields.add("provider");
        if (!StringUtils.hasText(request.getProviderId())) missingFields.add("providerId");

        if (!missingFields.isEmpty()) {
            throw new IllegalArgumentException(
                    "Required fields missing: " + String.join(", ", missingFields)
            );
        }

        validateAndGetProvider(request.getProvider());
    }

    /**
     * 회원가입 요청 유효성 검증
     * 필수 필드 존재 여부 및 Provider 유효성 검증
     */
    private void validateRegistrationRequest(OAuthMemberRegisterRequest request) {
        List<String> missingFields = new ArrayList<>();

        if (!StringUtils.hasText(request.getProviderId())) missingFields.add("providerId");
        if (!StringUtils.hasText(request.getProvider())) missingFields.add("provider");
        if (!StringUtils.hasText(request.getEmail())) missingFields.add("email");
        if (!StringUtils.hasText(request.getName())) missingFields.add("name");

        if (!missingFields.isEmpty()) {
            throw new IllegalArgumentException(
                    "Required fields missing: " + String.join(", ", missingFields)
            );
        }

        validateAndGetProvider(request.getProvider());
    }

    /**
     * 사용자 중복 검사
     * Provider와 ProviderId 조합의 중복 여부 확인
     */
    private void checkDuplicateUser(OAuthMemberRegisterRequest request) {
        AuthMemberInfo.Provider provider = validateAndGetProvider(request.getProvider());

        if (authMemberPort.findByProviderAndProviderId(
                provider.getValue(), request.getProviderId()) != null) {
            logger.warn("User already exists: provider={}, providerId={}",
                    provider.getValue(),
                    SecurityUtils.maskValue(request.getProviderId()));
            throw new UserAlreadyExistsException("User already exists");
        }
    }

    /**
     * 인증 성공 응답 생성
     */
    private AuthenticationResponse createSuccessfulAuthResponse(AuthMemberInfo authMemberInfo) {
        return AuthenticationResponse.builder()
                .success(true)
                .memberProfile(authMemberDomainMapper.toProfileResponse(authMemberInfo))
                .build();
    }

    /**
     * Provider와 ProviderId로 회원 조회
     */
    private AuthMemberInfo findMemberByProviderAndId(AuthMemberInfo.Provider provider, String providerId) {
        return Optional.ofNullable(
                        authMemberPort.findByProviderAndProviderId(
                                provider.getValue(),
                                providerId
                        )
                )
                .map(authMemberDomainMapper::toDomainModel)
                .orElseThrow(() -> new MemberNotFoundException(provider.getValue(), providerId));
    }

    /**
     * 도메인 이벤트 처리
     * MemberInfo에서 발생한 이벤트를 애플리케이션 이벤트로 발행
     */
    private void handleMemberEvents(AuthMemberInfo authMemberInfo) {
        authMemberInfo.getDomainEvents().forEach(event -> {
            logger.debug("Publishing domain event: {}", event.getClass().getSimpleName());
            eventPublisher.publishEvent(event);
        });
        authMemberInfo.clearDomainEvents();
    }
}

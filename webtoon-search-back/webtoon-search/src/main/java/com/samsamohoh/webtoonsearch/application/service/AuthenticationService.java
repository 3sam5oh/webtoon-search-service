package com.samsamohoh.webtoonsearch.application.service;

import com.samsamohoh.webtoonsearch.application.domain.MemberInfo;
import com.samsamohoh.webtoonsearch.application.exception.UserAlreadyExistsException;
import com.samsamohoh.webtoonsearch.application.port.in.member.AuthenticationUseCase;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.AuthenticationResponse;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.MemberProfileResponse;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.OAuthLoginRequest;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.OAuthMemberRegisterRequest;
import com.samsamohoh.webtoonsearch.application.port.out.MemberPersistencePort;
import com.samsamohoh.webtoonsearch.application.port.out.dto.MemberPersistenceResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationUseCase {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final MemberPersistencePort memberPersistencePort;

    @Override
    @Transactional
    public AuthenticationResponse authenticateMember(OAuthLoginRequest oAuthLoginRequest) throws AuthenticationException {
        logger.info("Authenticating member with provider: {}", oAuthLoginRequest.getProvider());
        MemberPersistenceResponse memberPersistenceResponse = memberPersistencePort.findMemberByProviderId(oAuthLoginRequest.getUsername());
        if (memberPersistenceResponse == null) {
            logger.warn("User not found with provider id: {}", oAuthLoginRequest.getUsername());
            throw new UsernameNotFoundException("User not found with provider id: " + oAuthLoginRequest.getUsername());
        }
        MemberInfo memberInfo = convertToDomainModel(memberPersistenceResponse);
        logger.info("Member authenticated successfully: {}", memberInfo.getEmail());
        return new AuthenticationResponse(true, convertToProfileDTO(memberInfo));
    }

    @Override
    @Transactional
    public MemberProfileResponse registerNewMember(OAuthMemberRegisterRequest request) throws UserAlreadyExistsException {
        logger.info("Registering new member with provider: {}", request.getProvider());
        if (memberPersistencePort.findMemberByProviderId(request.getProviderId()) != null) {
            logger.warn("User already exists with provider id: {}", request.getProviderId());
            throw new UserAlreadyExistsException("User already exists with provider id: " + request.getProviderId());
        }
        MemberInfo newMember = MemberInfo.builder()
                .providerId(request.getProviderId())
                .provider(request.getProvider())
                .email(request.getEmail())
                .username(request.getUsername())
                .role(request.getRole())
                .age(request.getAge())
                .gender(request.getGender())
                .build();
        MemberPersistenceResponse savedMember = memberPersistencePort.saveMember(convertToPersistenceDTO(newMember));
        logger.info("New member registered successfully: {}", savedMember.getEmail());
        return convertToProfileDTO(convertToDomainModel(savedMember));
    }

    @Override
    public void logoutMember(String memberId) throws UsernameNotFoundException {

    }

    @Override
    public MemberProfileResponse getCurrentUserProfile(String memberId) throws UsernameNotFoundException {
        return null;
    }

    @Override
    public void withdrawMember(String memberId) throws UsernameNotFoundException {

    }

    // Other methods (logoutMember, getCurrentUserProfile, withdrawMember) ...

    private MemberInfo convertToDomainModel(MemberPersistenceResponse dto) {
        return MemberInfo.builder()
                .id(dto.getId())
                .providerId(dto.getProviderId())
                .provider(dto.getProvider())
                .email(dto.getEmail())
                .username(dto.getUsername())
                .role(dto.getRole())
                .age(dto.getAge())
                .gender(dto.getGender())
                .build();
    }

    private MemberProfileResponse convertToProfileDTO(MemberInfo memberInfo) {
        return MemberProfileResponse.builder()
                .id(memberInfo.getId())
                .providerId(memberInfo.getProviderId())
                .provider(memberInfo.getProvider())
                .email(memberInfo.getEmail())
                .username(memberInfo.getUsername())
                .role(memberInfo.getRole())
                .age(memberInfo.getAge())
                .build();
    }

    private MemberPersistenceResponse convertToPersistenceDTO(MemberInfo memberInfo) {
        return MemberPersistenceResponse.builder()
                .id(memberInfo.getId())
                .providerId(memberInfo.getProviderId())
                .provider(memberInfo.getProvider())
                .email(memberInfo.getEmail())
                .username(memberInfo.getUsername())
                .role(memberInfo.getRole())
                .age(memberInfo.getAge())
                .gender(memberInfo.getGender())
                .build();
    }
}

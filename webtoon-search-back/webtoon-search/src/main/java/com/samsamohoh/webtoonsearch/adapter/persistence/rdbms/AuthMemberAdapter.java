package com.samsamohoh.webtoonsearch.adapter.persistence.rdbms;

import com.samsamohoh.webtoonsearch.adapter.persistence.rdbms.entity.AuthMemberEntity;
import com.samsamohoh.webtoonsearch.application.port.out.AuthMemberPort;
import com.samsamohoh.webtoonsearch.application.port.out.dto.AuthMemberResponse;
import com.samsamohoh.webtoonsearch.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthMemberAdapter implements AuthMemberPort {
    private static final Logger logger = LoggerFactory.getLogger(AuthMemberAdapter.class);

    private final AuthMemberJpaRepository authMemberJPARepository;
    private final AuthMemberPersistenceMapper memberMapper;

    @Override
    @Transactional
    public AuthMemberResponse saveMember(AuthMemberResponse authMemberResponse) {
        AuthMemberEntity memberEntity = memberMapper.toEntity(authMemberResponse);
        AuthMemberEntity savedEntity = authMemberJPARepository.save(memberEntity);

        logger.debug("Member saved: provider={}, email={}",
                savedEntity.getProvider(),
                SecurityUtils.maskEmail(savedEntity.getEmail()));

        return memberMapper.toResponse(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthMemberResponse findByProviderAndProviderId(String provider, String providerId) {
        AuthMemberEntity entity = authMemberJPARepository
                .findByProviderAndProviderId(provider, providerId);

        if (entity != null) {
            logger.debug("Member found: provider={}, email={}",
                    provider,
                    SecurityUtils.maskEmail(entity.getEmail()));
        }

        return entity != null ? memberMapper.toResponse(entity) : null;
    }
}

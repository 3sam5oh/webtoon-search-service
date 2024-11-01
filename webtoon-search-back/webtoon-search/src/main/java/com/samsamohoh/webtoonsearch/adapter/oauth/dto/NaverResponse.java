package com.samsamohoh.webtoonsearch.adapter.oauth.dto;

import com.samsamohoh.webtoonsearch.application.port.in.member.dto.OAuth2Response;
import com.samsamohoh.webtoonsearch.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 네이버 OAuth2 응답을 처리하는 어댑터
 * 네이버 특화된 응답 구조를 애플리케이션의 OAuth2Response 형식으로 변환
 */
public class NaverResponse implements OAuth2Response {
    private static final Logger logger = LoggerFactory.getLogger(NaverResponse.class);

    /**
     * 네이버 API 응답 필드명 정의
     * @see <a href="https://developers.naver.com/docs/login/profile/profile.md">네이버 API 문서</a>
     */
    private static final String FIELD_ID = "id";          // 네이버 고유 식별자
    private static final String PROVIDER = "naver";       // OAuth 제공자
    private static final String FIELD_EMAIL = "email";    // 이메일
    private static final String FIELD_NAME = "name";      // 이름
    private static final String FIELD_AGE = "age";        // 연령대 (예: "20-29")
    private static final String FIELD_GENDER = "gender";  // 성별 (M/F)

    // 네이버 API 응답 데이터
    private final Map<String, Object> attributes;

    /**
     * NaverResponse 생성자
     * 네이버 OAuth2 응답 데이터의 유효성을 검증하고 초기화
     *
     * @param attributes OAuth2 응답 데이터
     * @throws IllegalArgumentException 필수 필드가 누락되었거나 유효하지 않은 경우
     */
    public NaverResponse(Map<String, Object> attributes) {
        validateAttributes(attributes);
        this.attributes = extractResponse(attributes);
        validateRequiredFields();
        logger.debug("NaverResponse created successfully with provider ID: {}",
                SecurityUtils.maskValue(getProviderID()));
    }

    // OAuth2Response 인터페이스 구현
    @Override
    public String getProviderID() {
        return getAttributeAsString(FIELD_ID);
    }

    @Override
    public String getProvider() {
        return PROVIDER;
    }

    @Override
    public String getEmail() {
        return getAttributeAsString(FIELD_EMAIL);
    }

    // 추가 프로필 정보 접근자
    @Override
    public String getName() {
        return getAttributeAsString(FIELD_NAME);
    }

    public String getAgeRange() {
        return getAttributeAsString(FIELD_AGE);
    }

    public String getGender() {
        String gender = getAttributeAsString(FIELD_GENDER);
        return gender != null ? gender.toUpperCase() : null;
    }

    /**
     * 응답 데이터 초기 검증
     */
    private void validateAttributes(Map<String, Object> attributes) {
        if (attributes == null) {
            logger.error("OAuth2 attributes cannot be null");
            throw new IllegalArgumentException("OAuth2 attributes cannot be null");
        }
    }

    /**
     * 네이버 API 응답 구조에서 실제 사용자 정보 추출
     */
    private Map<String, Object> extractResponse(Map<String, Object> attributes) {
        Object response = attributes.get("response");
        if (response == null) {
            logger.error("Naver OAuth2 response is null");
            throw new IllegalArgumentException("Naver OAuth2 response is null");
        }

        if (!(response instanceof Map)) {
            logger.error("Invalid Naver OAuth2 response structure. Expected Map but got: {}",
                    response.getClass().getName());
            throw new IllegalArgumentException("Invalid Naver OAuth2 response structure");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = (Map<String, Object>) response;

        // 응답 맵이 비어있는지 확인
        if (responseMap.isEmpty()) {
            logger.error("Naver OAuth2 response map is empty");
            throw new IllegalArgumentException("Naver OAuth2 response map is empty");
        }

        return responseMap;
    }

    /**
     * 필수 필드 존재 여부 검증
     */
    private void validateRequiredFields() {
        List<String> missingFields = new ArrayList<>();

        if (getProviderID() == null) missingFields.add(FIELD_ID);
        if (getEmail() == null) missingFields.add(FIELD_EMAIL);
        if (getName() == null) missingFields.add(FIELD_NAME);

        if (!missingFields.isEmpty()) {
            String message = "Missing required fields: " + String.join(", ", missingFields);
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 속성값을 문자열로 안전하게 변환
     */
    private String getAttributeAsString(String key) {
        Object value = attributes.get(key);
        if (value == null) {
            logger.debug("Attribute '{}' is missing in Naver OAuth2 response", key);
            return null;
        }

        String stringValue = value.toString().trim();
        if (stringValue.isEmpty()) {
            logger.warn("Attribute '{}' is empty in Naver OAuth2 response", key);
            return null;
        }

        return stringValue;
    }

    @Override
    public String toString() {
        return String.format("NaverResponse{id='%s', email='%s', name='%s', provider='%s'}",
                SecurityUtils.maskValue(getProviderID()),
                SecurityUtils.maskEmail(getEmail()),
                SecurityUtils.maskName(getName()),
                getProvider()
        );
    }
}

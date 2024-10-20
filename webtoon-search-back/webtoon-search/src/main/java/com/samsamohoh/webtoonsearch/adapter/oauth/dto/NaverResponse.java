package com.samsamohoh.webtoonsearch.adapter.oauth.dto;

import com.samsamohoh.webtoonsearch.application.port.in.member.dto.OAuth2Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class NaverResponse implements OAuth2Response {
    private static final Logger logger = LoggerFactory.getLogger(NaverResponse.class);
    private final Map<String, Object> attributes;

    public NaverResponse(Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        if (response == null) {
            logger.error("Naver OAuth2 response is null or does not contain 'response' key");
            throw new IllegalArgumentException("Invalid Naver OAuth2 response");
        }
        this.attributes = response;
        logger.debug("Naver OAuth2 response received: {}", this.attributes);
    }

    @Override
    public String getProviderID() {
        return getAttributeAsString("id");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getEmail() {
        return getAttributeAsString("email");
    }

    public String getName() {
        return getAttributeAsString("name");
    }

    public String getAge() {
        return getAttributeAsString("age");
    }

    public String getGender() {
        return getAttributeAsString("gender");
    }

    private String getAttributeAsString(String key) {
        Object value = attributes.get(key);
        if (value == null) {
            logger.warn("Attribute '{}' is missing in Naver OAuth2 response", key);
            return null;
        }
        return value.toString();
    }

    @Override
    public String toString() {
        return "NaverResponse{" +
                "id='" + getProviderID() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", name='" + getName() + '\'' +
                '}';
    }
}

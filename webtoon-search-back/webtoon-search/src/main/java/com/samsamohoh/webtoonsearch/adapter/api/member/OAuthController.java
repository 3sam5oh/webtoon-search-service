package com.samsamohoh.webtoonsearch.adapter.api.member;

import com.samsamohoh.webtoonsearch.common.ApiResponse;
import com.samsamohoh.webtoonsearch.application.port.in.member.AuthenticationUseCase;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.MemberProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuthController {

    private final AuthenticationUseCase authenticationUseCase;

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(
                ApiResponse.success("Health check successful", "OAuthController healthCheck")
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 인증 검사
        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof OAuth2User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        try {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oauth2User.getAttributes();

            MemberProfileResponse profile = authenticationUseCase.getCurrentUserProfile(
                    (String) attributes.get("provider"),
                    (String) attributes.get("provider_id")
            );

            return ResponseEntity.ok(
                    ApiResponse.success("User profile retrieved successfully", profile)
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve user profile"));
        }
    }

    @GetMapping("/login")
    public ResponseEntity<ApiResponse<String>> login() {
        return ResponseEntity.ok(
                ApiResponse.success("OAuth2 login endpoint", "You should be redirected to the OAuth2 provider.")
        );
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(
                ApiResponse.success("Logout endpoint", "You should be logged out.")
        );
    }

    @GetMapping("/login-success")
    public ResponseEntity<ApiResponse<String>> loginSuccess() {
        return ResponseEntity.ok(
                ApiResponse.success("Login successful", "login-success")
        );
    }

    @GetMapping("/login-failure")
    public ResponseEntity<ApiResponse<String>> loginFailure() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Login failed"));
    }
}

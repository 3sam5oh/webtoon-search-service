package com.samsamohoh.webtoonsearch.adapter.api.member;

import com.samsamohoh.webtoonsearch.adapter.api.ApiResponse;
import com.samsamohoh.webtoonsearch.application.port.in.member.AuthenticationUseCase;
import com.samsamohoh.webtoonsearch.application.port.in.member.dto.MemberProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuthController {

    private final AuthenticationUseCase authenticationUseCase;

    @GetMapping("/health")
    public String healthCheck() {
        return "OAuthController healthCheck";
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(new ApiResponse<>(null));
        }

        String username = authentication.getName();
        MemberProfileResponse profile = authenticationUseCase.getCurrentUserProfile(username);
        return ResponseEntity.ok(new ApiResponse<>(profile));
    }

    @GetMapping("/login")
    public ResponseEntity<String> login() {
        // This endpoint will be handled by Spring Security
        // It's just here to have a defined endpoint for the OAuth2 login
        return ResponseEntity.ok("You should be redirected to the OAuth2 provider.");
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        // This endpoint will be handled by Spring Security
        // It's just here to have a defined endpoint for logout
        return ResponseEntity.ok("You should be logged out.");
    }

    @GetMapping("/login-success")
    public String loginSuccess() {
        return "login-success";  // login-success.html 템플릿을 반환
    }

    @GetMapping("/login-failure")
    public String loginFailure() {
        return "login-failure";  // login-failure.html 템플릿을 반환
    }
}

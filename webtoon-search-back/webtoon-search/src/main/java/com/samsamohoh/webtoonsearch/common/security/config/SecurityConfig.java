package com.samsamohoh.webtoonsearch.common.security.config;

import com.samsamohoh.webtoonsearch.application.port.out.SaveMemberPort;
import com.samsamohoh.webtoonsearch.application.service.PrincipalOauth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SaveMemberPort saveMemberPort;

    // SaveMemberPort를 생성자로 주입받도록 수정합니다.
    public SecurityConfig(SaveMemberPort saveMemberPort) {
        this.saveMemberPort = saveMemberPort;
    }

    @Bean
    public PrincipalOauth2UserService principalOauth2UserService() {
        return new PrincipalOauth2UserService(saveMemberPort);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 필요한 경우 CSRF 설정을 조정하세요.
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/logins/**").permitAll() // API 엔드포인트에 대한 접근 허용
                                .requestMatchers("/webtoons/**").permitAll()
                                .requestMatchers("/actuator/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .loginPage("/")
                                .defaultSuccessUrl("http://localhost:8081")
                                .failureUrl("/")
                                .userInfoEndpoint(userInfoEndpoint ->
                                        userInfoEndpoint.userService(principalOauth2UserService())
                                )
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("http://localhost:8081")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .headers(headers -> headers
                        // X-Frame-Options 문제 해결: 동일 출처에서 iframe 허용
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                .cors(withDefaults()); // 기본 CORS 설정 적용

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8081"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}


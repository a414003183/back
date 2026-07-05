package com.telecom.scm.security.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.telecom.scm.common.enums.MemberTypeEnum;
import com.telecom.scm.security.filter.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Value(
            "${app.security.cors.allowed-origin-patterns:http://localhost:*,http://127.0.0.1:*,http://192.168.*:*,http://10.*:*,http://172.*:*}")
    private String allowedOriginPatterns;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            RestAccessDeniedHandler restAccessDeniedHandler)
            throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(
                        exception ->
                                exception
                                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                                        .accessDeniedHandler(restAccessDeniedHandler))
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers(
                                                "/api/health",
                                                "/api/auth/login",
                                                "/api/auth/register/customer",
                                                "/api/auth/register/merchant",
                                                "/api/auth/register/supplier")
                                        .permitAll()
                                        .requestMatchers(HttpMethod.GET, "/api/files/**")
                                        .permitAll()
                                        .requestMatchers("/api/mall/cart/**")
                                        .hasRole(MemberTypeEnum.CUSTOMER.getCode())
                                        .requestMatchers(HttpMethod.GET, "/api/mall/**")
                                        .permitAll()
                                        .requestMatchers("/api/member/customer/**")
                                        .hasRole(MemberTypeEnum.CUSTOMER.getCode())
                                        .requestMatchers("/api/member/merchant/**")
                                        .hasRole(MemberTypeEnum.MERCHANT.getCode())
                                        .requestMatchers("/api/member/supplier/**")
                                        .hasRole(MemberTypeEnum.SUPPLIER.getCode())
                                        .requestMatchers("/api/admin/**")
                                        .hasRole(MemberTypeEnum.ADMIN.getCode())
                                        .requestMatchers("/api/app/**")
                                        .hasRole(MemberTypeEnum.CUSTOMER.getCode())
                                        .anyRequest()
                                        .authenticated())
                .addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(parseAllowedOriginPatterns());
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Content-Disposition"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> parseAllowedOriginPatterns() {
        return Arrays.stream(allowedOriginPatterns.split(","))
                .map(String::trim)
                .filter(pattern -> !pattern.isEmpty())
                .collect(Collectors.toList());
    }
}

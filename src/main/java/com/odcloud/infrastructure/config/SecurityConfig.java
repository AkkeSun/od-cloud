package com.odcloud.infrastructure.config;

import com.odcloud.infrastructure.filter.ApiCallLogFilter;
import com.odcloud.infrastructure.filter.JwtAuthenticationFilter;
import com.odcloud.infrastructure.handler.CustomAccessDeniedHandler;
import com.odcloud.infrastructure.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ApiCallLogFilter logFilter;
    private final JwtAuthenticationFilter jwtFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // --------------- 인증 정책 ---------------
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> {
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            })
            .addFilterBefore(logFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

            // --------------- 인가 정책 ---------------
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/docs/**").permitAll()
                    
                    .requestMatchers("/**").permitAll() // for test

                    .requestMatchers("/favicon.ico").permitAll()
                    .requestMatchers("/accounts").permitAll()
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers("/accounts/{username}/approval").hasRole("ADMIN")
                    .anyRequest().authenticated();
            })

            // --------------- 인증/인가 예외처리 ---------------
            .exceptionHandling(exception -> {
                exception.authenticationEntryPoint(customAuthenticationEntryPoint);
                exception.accessDeniedHandler(customAccessDeniedHandler);
            })

            //--------------- csrf, cors 설정 ---------------
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        ;
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

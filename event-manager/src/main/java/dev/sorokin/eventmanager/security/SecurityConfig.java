package dev.sorokin.eventmanager.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventmanager.exception.ErrorMessageResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.time.OffsetDateTime;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    public SecurityConfig(ObjectMapper objectMapper, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.objectMapper = objectMapper;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.POST,"/users")
                                .permitAll()
                                .requestMatchers(HttpMethod.POST,"/users/auth")
                                .permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                                .permitAll()
                                .anyRequest().authenticated()


                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex-> ex.authenticationEntryPoint(authenticationEntryPoint()))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {

        return (request, response, authException) ->{
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8 ");
            var error = new ErrorMessageResponse();
            error.setMessage("Необходима аутентификация");
            error.setDetailedMessage(authException.getMessage());
            error.setDateTime(OffsetDateTime.now());
            var json = objectMapper.writeValueAsString(error);
            response.getWriter().write(json);
        };


    }
}

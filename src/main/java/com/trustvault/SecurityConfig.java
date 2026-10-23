
package com.trustvault;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        CookieCsrfTokenRepository csrfRepository =
                CookieCsrfTokenRepository.withHttpOnlyFalse();

        CsrfTokenRequestAttributeHandler requestHandler =
                new CsrfTokenRequestAttributeHandler();

        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfRepository)
                .csrfTokenRequestHandler(requestHandler)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/status").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.permitAll());

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<OncePerRequestFilter> csrfTokenFilter() {
        FilterRegistrationBean<OncePerRequestFilter> registration =
                new FilterRegistrationBean<>();

        registration.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain)
                    throws ServletException, IOException {

                CsrfToken token = (CsrfToken) request.getAttribute(
                        CsrfToken.class.getName());

                if (token != null) {
                    token.getToken();
                }

                filterChain.doFilter(request, response);
            }
        });

        registration.setOrder(1);
        return registration;
    }
}

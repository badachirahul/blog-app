package com.mountblue.blog_app.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // public
                        .requestMatchers(HttpMethod.GET, "/blog/home").permitAll()
                        .requestMatchers("/auth/login", "/auth/register").permitAll()

                        // post CRUD - AUTHOR or ADMIN (specific rules first!)
                        .requestMatchers(HttpMethod.GET, "/blog/posts/new").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/blog/posts").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/blog/posts/update/**").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/blog/posts/update").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/blog/posts/delete/**").hasAnyRole("AUTHOR", "ADMIN")

                        // comment update/delete - authenticated
                        .requestMatchers(HttpMethod.GET, "/blog/posts/{postId}/comments/{commentId}/update").authenticated()
                        .requestMatchers(HttpMethod.POST, "/blog/posts/{postId}/comments/{commentId}/update").authenticated()
                        .requestMatchers(HttpMethod.POST, "/blog/posts/{postId}/comments/{commentId}/delete").authenticated()

                        // wildcard public routes AFTER specific rules
                        .requestMatchers(HttpMethod.GET, "/blog/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/blog/posts/{postId}/comments").permitAll()

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .defaultSuccessUrl("/blog/home", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/blog/home")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendRedirect("/error/403"))
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect("/auth/login?unauthorized"))
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
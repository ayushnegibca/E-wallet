package org.gfg.TransactionService.config;

import org.gfg.TransactionService.JWTFilter;
import org.gfg.TransactionService.jwt.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    UserDetailsService userDetailsService;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        JWTFilter jwtFilter = new JWTFilter(jwtUtil,userDetailsService);
       return httpSecurity.csrf(csrf->csrf.disable())
                .authorizeHttpRequests(request->
                        request.anyRequest().authenticated())
             //   .httpBasic(Customizer.withDefaults())
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}

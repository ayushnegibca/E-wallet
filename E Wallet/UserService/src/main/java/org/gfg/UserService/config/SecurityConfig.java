package org.gfg.UserService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity){
        httpSecurity.csrf(
                csrf->csrf.disable()
        ).authorizeHttpRequests(request->
                        request.requestMatchers("/user-service/api/user/create","/user-service/api/otp/validate","/user-service/api/login","/user-service/api/validate/user/**").permitAll()
                                .anyRequest().authenticated()
                                )
                .httpBasic(Customizer.withDefaults());
       return httpSecurity.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

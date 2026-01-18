package org.gfg.WalletService.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    RestTemplate restTemplate;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String url = "http://localhost:8080/user-service/api/validate/user/"+username;

        String response = restTemplate.getForObject(url,String.class);
        if (response!=null && response.equals("OK")){
            UserDetails userDetails = User.builder().username(username).password("").build();
            return userDetails;
        }else{
            return null;
        }
    }
}

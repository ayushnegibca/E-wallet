package org.gfg.WalletService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GenericConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}

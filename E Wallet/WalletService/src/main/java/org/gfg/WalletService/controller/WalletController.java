package org.gfg.WalletService.controller;

import org.gfg.WalletService.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wallet-service/api")
public class WalletController {

    @Autowired
    WalletService walletService;

    @GetMapping("/wallet/balance")
    public Double walletBalance(){
        UserDetails userDetails =  (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String mobile = userDetails.getUsername();
        return walletService.walletBalance(mobile);
    }
}

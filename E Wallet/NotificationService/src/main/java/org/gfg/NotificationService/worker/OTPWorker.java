package org.gfg.NotificationService.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class OTPWorker {


    @Autowired
    JavaMailSender javaMailSender;

    public void sendOTP(String email, String otp){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("gfgwallet64@gmail.com");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Wallet OTP Verification");
        simpleMailMessage.setText("Your one time otp for verification is "+otp+" valid till 3 minutes, please do not share it");
        javaMailSender.send(simpleMailMessage);
        System.out.println("OTP send to user");
    }
}

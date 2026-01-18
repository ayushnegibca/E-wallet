package org.gfg.NotificationService.consumer;

import org.gfg.CommonConstants;
import org.gfg.NotificationService.worker.OTPWorker;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OTPMessageConsumer {

    @Autowired
    OTPWorker otpWorker;

    @KafkaListener(topics = CommonConstants.USER_OTP_TOPIC, groupId = "otp-group")
    public void consumeOTPMessage(String data){
        System.out.println("Kafka message: "+data);
        JSONObject jsonObject = new JSONObject(data);
        String email = jsonObject.getString(CommonConstants.USER_EMAIL);
        String otp = jsonObject.getString(CommonConstants.USER_OTP);

        otpWorker.sendOTP(email,otp);
    }
}

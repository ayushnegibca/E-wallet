package org.gfg.WalletService.consumer;

import netscape.javascript.JSObject;
import org.gfg.CommonConstants;
import org.gfg.WalletService.service.WalletService;
import org.gfg.model.DocumentType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class WalletConsumer {

    @Autowired
    WalletService walletService;


    @KafkaListener(topics = CommonConstants.USER_CREATED_TOPIC, groupId = "wallet-group")
    public void consumeNewlyCreatedUser(String data){
        JSONObject jsonObject = new JSONObject(data);

        walletService.createAndPersistWallet(jsonObject);

    }
}

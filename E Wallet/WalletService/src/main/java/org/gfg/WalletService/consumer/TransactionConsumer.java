package org.gfg.WalletService.consumer;

import org.gfg.CommonConstants;
import org.gfg.WalletService.service.WalletService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

    @Autowired
    WalletService walletService;


    @KafkaListener(topics = CommonConstants.TXN_CREATION_TOPIC, groupId = "txn-create-group")
    public void consumerTransactions(String data){
        System.out.println("Data Received: "+data);

        JSONObject txnMessage = new JSONObject(data);

        String senderId = txnMessage.optString(CommonConstants.SENDER_ID);
        String receiverId = txnMessage.optString(CommonConstants.RECEIVER_ID);
        double txnAmount = txnMessage.optDouble(CommonConstants.TXN_AMOUNT);
        String txnId = txnMessage.optString(CommonConstants.TXN_ID);

        walletService.makeTransaction(senderId,receiverId,txnId,txnAmount);
    }
}

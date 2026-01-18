package org.gfg.TransactionService.Listener;

import org.gfg.CommonConstants;
import org.gfg.TransactionService.repository.TransactionRepository;
import org.gfg.model.TransactionStatus;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UpdatedTransactionListener {

    @Autowired
    TransactionRepository transactionRepository;


    @KafkaListener(topics = CommonConstants.TXN_UPDATE_TOPIC, groupId = "txn-update-group")
    public void receiveUpdatedTransaction(String data){
        System.out.println("Updated Txn data: "+data);

        JSONObject jsonObject = new JSONObject(data);
        String txnId = jsonObject.optString(CommonConstants.TXN_ID);
        TransactionStatus transactionStatus = jsonObject.optEnum(TransactionStatus.class,CommonConstants.TXN_STATUS);
        String transactionMessage = jsonObject.optString(CommonConstants.TXN_MESSAGE);

        int rows = transactionRepository.updateTransaction(txnId,transactionStatus,transactionMessage);

        if (rows>0){
            System.out.println("Final Transaction Updated");
        }

    }
}

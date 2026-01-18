package org.gfg.TransactionService.service;

import org.gfg.CommonConstants;
import org.gfg.TransactionService.model.Transaction;
import org.gfg.TransactionService.model.TransactionResponse;
import org.gfg.TransactionService.model.TxnType;
import org.gfg.TransactionService.repository.TransactionRepository;
import org.gfg.model.TransactionStatus;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {


    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    public String initiateTransaction(String senderId, String receiverId, String purpose, double amount){
        String txnId = UUID.randomUUID().toString();
        Transaction transaction = Transaction.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .txnId(txnId)
                .amount(amount)
                .purpose(purpose)
                .txnStatus(TransactionStatus.INITIATED)
                .txnMessage("Transaction is Initiated")
                .build();

       Transaction received = transactionRepository.save(transaction);
       if (received!=null){
           JSONObject txnMessage = new JSONObject();
           txnMessage.put(CommonConstants.TXN_ID,txnId);
           txnMessage.put(CommonConstants.SENDER_ID,senderId);
           txnMessage.put(CommonConstants.RECEIVER_ID,receiverId);
           txnMessage.put(CommonConstants.TXN_AMOUNT,amount);
           kafkaTemplate.send(CommonConstants.TXN_CREATION_TOPIC,txnMessage.toString());

           System.out.println("Transaction data send to kafka");
       }
        return txnId;
    }


    public List<TransactionResponse> getTransactionHistory(String mobileNo){
       List<Transaction> transactionList =  transactionRepository.findBySenderIdOrReceiverId(mobileNo,mobileNo);

       List<TransactionResponse> ans = new ArrayList<>();

       for (Transaction t: transactionList){
           TransactionResponse transactionResponse = TransactionResponse.builder()
                   .txnStatus(t.getTxnStatus())
                   .amount(t.getAmount())
                   .txnTime(t.getTransactionTime())
                   .txnId(t.getTxnId())
                   .build();

           if (t.getSenderId().equals(mobileNo)){
               transactionResponse.setParty(t.getReceiverId());
               transactionResponse.setTxnType(TxnType.DEBIT);
           }else {
               transactionResponse.setParty(t.getSenderId());
               transactionResponse.setTxnType(TxnType.CREDIT);
           }
           ans.add(transactionResponse);
       }

       return ans;
    }

}

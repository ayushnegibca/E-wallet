package org.gfg.WalletService.service;

import org.gfg.CommonConstants;
import org.gfg.WalletService.model.Wallet;
import org.gfg.WalletService.repository.WalletRepository;
import org.gfg.model.DocumentType;
import org.gfg.model.TransactionStatus;
import org.gfg.model.WalletStatus;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
public class WalletService {


    @Value("${wallet.initial.amount}")
    private String initialBalance;


    @Autowired
    WalletRepository walletRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;


    public void createAndPersistWallet(JSONObject jsonObject){
        String mobile = jsonObject.optString(CommonConstants.USER_MOBILE);
        String email = jsonObject.optString(CommonConstants.USER_EMAIL);
        DocumentType documentType = jsonObject.optEnum(DocumentType.class,CommonConstants.USER_DOCUMENT_TYPE);
        String documentValue = jsonObject.optString(CommonConstants.USER_DOCUMENT_VALUE);
        int userId = jsonObject.optInt(CommonConstants.USER_ID);

        Wallet wallet = Wallet.builder()
                .mobileNo(mobile)
                .documentNo(documentValue)
                .documentType(documentType)
                .userId(userId)
                .status(WalletStatus.ACTIVE)
                .balance(Double.parseDouble(initialBalance))
                .email(email)
                .build();

        walletRepository.save(wallet);

        System.out.println("Wallet account created");
    }

    public void makeTransaction(String senderId, String receiverId, String txnId,double amount){
        Wallet senderWallet = walletRepository.findByMobileNo(senderId);
        Wallet receiverWallet = walletRepository.findByMobileNo(receiverId);

        TransactionStatus transactionStatus;
        String transactionMessage;

        if (senderWallet==null){
            transactionStatus = TransactionStatus.FAILED;
            transactionMessage = "Sender Wallet does not exist";
        }else if (receiverWallet==null){
            transactionStatus = TransactionStatus.FAILED;
            transactionMessage = "receiver Wallet does not exist";
        }else if (senderWallet.getBalance()<amount){
            transactionStatus = TransactionStatus.FAILED;
            transactionMessage = "Insufficient Balance";
        }else if (senderWallet.getStatus()==WalletStatus.BLOCKED){
            transactionStatus = TransactionStatus.FAILED;
            transactionMessage = "Sender wallet is blocked";
        }else if (receiverWallet.getStatus()==WalletStatus.BLOCKED){
            transactionStatus = TransactionStatus.FAILED;
            transactionMessage = "Receiver Wallet is Blocked";
        }else {
            if (makeWalletOperation(senderId,receiverId,amount)){
                transactionStatus = TransactionStatus.SUCCESS;
                transactionMessage = "Transaction is successfull";
            }else {
                transactionStatus = TransactionStatus.PENDING;
                transactionMessage = "Transaction is Pending";
            }
        }
        JSONObject txnUpdateMessage = new JSONObject();
        txnUpdateMessage.put(CommonConstants.TXN_ID,txnId);
        txnUpdateMessage.put(CommonConstants.TXN_STATUS,transactionStatus);
        txnUpdateMessage.put(CommonConstants.TXN_MESSAGE,transactionMessage);

        kafkaTemplate.send(CommonConstants.TXN_UPDATE_TOPIC,txnUpdateMessage.toString());

        System.out.println("Updated data send to kafka");
    }

    @Transactional
    public boolean makeWalletOperation(String senderId,String receiverId, double amount){
        boolean isTransactionDone = true;
        try {
            walletRepository.updateWalletBalance(senderId, -amount);
            walletRepository.updateWalletBalance(receiverId, amount);
        }
        catch (Exception e){
            isTransactionDone = false;
        }
        return isTransactionDone;
    }


    public Double walletBalance(String mobile){
       Wallet wallet =  walletRepository.findByMobileNo(mobile);
       if (wallet!=null){
           return wallet.getBalance();
       }
       return null;
    }
}

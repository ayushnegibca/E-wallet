package org.gfg.TransactionService.controller;

import org.gfg.TransactionService.model.TransactionResponse;
import org.gfg.TransactionService.request.TransactionInitiateRequest;
import org.gfg.TransactionService.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/txn-service/api")
public class TransactionController {


    @Autowired
    TransactionService transactionService;

    @PostMapping("/initiate/transaction")
    public String initiateTransaction(@RequestBody TransactionInitiateRequest transactionInitiateRequest){
      UserDetails userDetails =  (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      String senderId = userDetails.getUsername();
      String receiverId = transactionInitiateRequest.getReceiverId();
      double amount = transactionInitiateRequest.getTxnAmount();
      String purpose = transactionInitiateRequest.getPurpose();

      return transactionService.initiateTransaction(senderId,receiverId,purpose,amount);
    }


    @GetMapping("/transaction/history")
    public List<TransactionResponse> transactionHistory(){
        UserDetails userDetails =  (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String senderId = userDetails.getUsername();
        return transactionService.getTransactionHistory(senderId);
    }
}

package org.gfg.TransactionService.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionInitiateRequest {

    private String receiverId;
    private double txnAmount;
    private String purpose;
}

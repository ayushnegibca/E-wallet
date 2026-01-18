package org.gfg.TransactionService.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gfg.model.TransactionStatus;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {

    private String party;
    private double amount;
    private String txnId;
    private Date txnTime;
    private TransactionStatus txnStatus;
    private TxnType txnType;
}

package org.gfg.TransactionService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gfg.model.TransactionStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "transaction")
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(unique = true)
    private String txnId;

    private String senderId;

    private String receiverId;

    private String purpose;

    double amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus txnStatus;

    private String txnMessage;

    @CreationTimestamp
    Date transactionTime;
}

package org.gfg.WalletService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gfg.model.DocumentType;
import org.gfg.model.WalletStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "wallet")
@Builder
public class Wallet {

    int userId;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int walletId;
    @Column(unique = true)
    String mobileNo;
    @Column(unique = true)
    String email;

    @Enumerated(EnumType.STRING)
    DocumentType documentType;

    @Column(unique = true)
    String documentNo;

    double balance;

    @Enumerated(EnumType.STRING)
    WalletStatus status;

    @CreationTimestamp
    Date createdOn;
}

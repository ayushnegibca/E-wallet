package org.gfg.TransactionService.repository;

import org.gfg.TransactionService.model.Transaction;
import org.gfg.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {

    @Transactional
    @Modifying
    @Query("update transaction t set t.txnStatus=:txnStatus, t.txnMessage=:txnMessage where t.txnId=:txnId")
    int updateTransaction(String txnId, TransactionStatus txnStatus,String txnMessage);


    List<Transaction> findBySenderIdOrReceiverId(String sender, String receiver);
}

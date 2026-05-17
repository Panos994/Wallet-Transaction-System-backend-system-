package wallet_transaction_system.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wallet_transaction_system.demo.entity.Transaction;
import wallet_transaction_system.demo.entity.TransactionStatus;
import wallet_transaction_system.demo.entity.TransactionType;

import java.util.List;
import java.util.UUID;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByFromWallet_IdOrToWallet_Id(UUID fromWalletId, UUID toWalletId);
    List<Transaction> findByStatus(TransactionStatus status);
    List<Transaction> findByType(TransactionType type);

}

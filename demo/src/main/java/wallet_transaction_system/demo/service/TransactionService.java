package wallet_transaction_system.demo.service;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import wallet_transaction_system.demo.dto.CreateTransactionResponseDTO;
import wallet_transaction_system.demo.dto.DepositRequestDTO;
import wallet_transaction_system.demo.dto.TransferRequestDTO;
import wallet_transaction_system.demo.dto.WithDrawRequestDTO;
import wallet_transaction_system.demo.entity.Transaction;
import wallet_transaction_system.demo.entity.TransactionStatus;
import wallet_transaction_system.demo.entity.TransactionType;
import wallet_transaction_system.demo.entity.Wallet;
import wallet_transaction_system.demo.exception.BadRequestException;
import wallet_transaction_system.demo.exception.ConcurrencyException;
import wallet_transaction_system.demo.exception.ForbiddenException;
import wallet_transaction_system.demo.repository.TransactionRepository;
import wallet_transaction_system.demo.repository.WalletRepository;
import wallet_transaction_system.demo.security.CustomUserDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Service
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private static final int MAX_RETRIES = 3;

    public TransactionService(TransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    //safe execution method:
    public <T> T executeWithRetry(Supplier<T> action){
        int attempts = 0;
        while(true){
            try{
                return action.get();
            } catch(ObjectOptimisticLockingFailureException ex){
                attempts++;
                log.warn("Retry attempt " + attempts + " due to concurrent update");
                if(attempts >= MAX_RETRIES){
                    throw new ConcurrencyException("Too many concurrent updates");
                }
            }
        }
    }



    public CreateTransactionResponseDTO deposit(UUID walletId, DepositRequestDTO dto){
        return executeWithRetry(() -> createDepositTransaction(walletId, dto));
    }

    @Transactional
    public CreateTransactionResponseDTO createDepositTransaction(UUID walletId, DepositRequestDTO dto){
        Wallet wallet = walletRepository.findByIdWithLock(walletId).orElseThrow(() -> new IllegalArgumentException("Wallet not found with id: " + walletId));


        UUID userId = getCurrentUserId();
        validateOwnership(wallet, userId);


        if(dto.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        wallet.setBalance(wallet.getBalance().add(dto.getAmount()));

        Transaction transaction = new Transaction();
        transaction.setToWallet(wallet);
        transaction.setAmount(dto.getAmount());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);

//        return CreateTransactionResponseDTO.builder()
//                .amount(transaction.getAmount())
//                .status(transaction.getStatus())
//                .type(transaction.getType())
//                .build();
        return map(transaction);
    }


    public CreateTransactionResponseDTO withdraw(UUID walletId, WithDrawRequestDTO dto) {

        return executeWithRetry(() -> createWithdrawTransaction(walletId, dto));
    }

    @Transactional
    public CreateTransactionResponseDTO createWithdrawTransaction(UUID walletId, WithDrawRequestDTO dto){

        Wallet wallet = walletRepository.findByIdWithLock(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found"));

        UUID userId = getCurrentUserId();
        validateOwnership(wallet, userId);

        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be positive");
        }

        if (wallet.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        wallet.setBalance(wallet.getBalance().subtract(dto.getAmount()));

        Transaction tx = new Transaction();
        tx.setFromWallet(wallet);
        tx.setAmount(dto.getAmount());
        tx.setType(TransactionType.WITHDRAW);
        tx.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(tx);
//        return CreateTransactionResponseDTO.builder()
//                .amount(transaction.getAmount())
//                .status(transaction.getStatus())
//                .type(transaction.getType())
//                .build();
        return map(tx);
    }

    public CreateTransactionResponseDTO transfer(UUID fromId, TransferRequestDTO dto){
        return executeWithRetry(() -> createTransferTransaction(fromId, dto));
    }
    @Transactional
    public CreateTransactionResponseDTO createTransferTransaction(UUID fromId,TransferRequestDTO dto){

        if (fromId.equals(dto.getTargetWalletId())) {
            throw new BadRequestException("Cannot transfer to same wallet");
        }

        Wallet from = walletRepository.findByIdWithLock(fromId)
                .orElseThrow(() -> new IllegalArgumentException("From Wallet not found"));
        Wallet to = walletRepository.findByIdWithLock(dto.getTargetWalletId()).orElseThrow(() -> new IllegalArgumentException("To Wallet not found"));

        UUID userId = getCurrentUserId();
        validateOwnership(from, userId);

        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be positive");
        }

        if(from.getBalance().compareTo(dto.getAmount()) < 0){
            throw new IllegalArgumentException("Insufficient balance");
        }
        from.setBalance(from.getBalance().subtract(dto.getAmount()));
        to.setBalance(to.getBalance().add(dto.getAmount()));
        Transaction transaction = new Transaction();
        transaction.setFromWallet(from);
        transaction.setToWallet(to);
        transaction.setAmount(dto.getAmount());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);
//        return CreateTransactionResponseDTO.builder()
//                .amount(transaction.getAmount())
//                .status(transaction.getStatus())
//                .type(transaction.getType())
//                .build();
        return map(transaction);
    }

    public List<Transaction> getTransactionsByWalletId(UUID walletId){
        return transactionRepository.findByFromWallet_IdOrToWallet_Id(walletId, walletId);
    }
    public List<Transaction> getTransactionsByStatus(TransactionStatus status){
        return transactionRepository.findByStatus(status);
    }

    public List<Transaction> getTransactionsByType(TransactionType type){
        return transactionRepository.findByType(type);
    }

    private CreateTransactionResponseDTO map(Transaction tx){
        return CreateTransactionResponseDTO.builder()
                .transactionId(tx.getId())
                .walletFromId(tx.getFromWallet() != null ? tx.getFromWallet().getId() : null)
                .walletToId(tx.getToWallet() != null ? tx.getToWallet().getId() : null)
                .amount(tx.getAmount())
                .type(tx.getType())
                .status(tx.getStatus())
                .build();
    }

    public void validateOwnership(Wallet wallet, UUID userId){
        if(!wallet.getUser().getId().equals(userId)){
            throw new ForbiddenException("Access denied to this wallet");
        }
    }

    public UUID getCurrentUserId(){
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getUser().getId();
    }
}

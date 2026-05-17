package wallet_transaction_system.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wallet_transaction_system.demo.dto.*;
import wallet_transaction_system.demo.entity.Transaction;

import wallet_transaction_system.demo.entity.TransactionStatus;
import wallet_transaction_system.demo.entity.TransactionType;
import wallet_transaction_system.demo.service.TransactionService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TranscationController {

    private final TransactionService transactionService;

    public TranscationController(TransactionService transcationService) {
        this.transactionService = transcationService;
    }


    @PostMapping("/wallets/{walletId}/deposit")
    public ResponseEntity<CreateTransactionResponseDTO> createDeposit(
            @PathVariable UUID walletId,
            @RequestBody DepositRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.createDepositTransaction(walletId, dto));
    }

    @PostMapping("/wallets/{walletId}/withdraw")
    public ResponseEntity<CreateTransactionResponseDTO> createWithDraw(@RequestBody WithDrawRequestDTO dto, @PathVariable UUID walletId){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createWithdrawTransaction(walletId,dto));
    }
    @PostMapping("/wallets/{walletId}/transfer")
    public ResponseEntity<CreateTransactionResponseDTO> createTransfer(@RequestBody TransferRequestDTO dto, @PathVariable UUID walletId){
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransferTransaction(walletId,dto));
    }

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<List<CreateTransactionResponseDTO>> getByWallet(@PathVariable UUID walletId){
        return ResponseEntity.ok().body(mapToResponse(transactionService.getTransactionsByWalletId(walletId)));
    }

    @GetMapping("/status/{walletId}")
    public ResponseEntity<List<CreateTransactionResponseDTO>> getTransactionsByStatus(@PathVariable UUID walletId, @RequestParam TransactionStatus status){
        return ResponseEntity.ok().body(mapToResponse(transactionService.getTransactionsByStatus(status)));
    }


    @GetMapping("/type/{walletId}")
    public ResponseEntity<List<CreateTransactionResponseDTO>> getTransactionsByType(@PathVariable UUID walletId, @RequestParam TransactionType type){
        return ResponseEntity.ok().body(mapToResponse(transactionService.getTransactionsByType(type)));
    }


    private List<CreateTransactionResponseDTO> mapToResponse(List<Transaction> transactions){
        return transactions.stream().map(this::mapToResponse).toList();
    }
    private CreateTransactionResponseDTO mapToResponse(Transaction transaction){
        return CreateTransactionResponseDTO.builder()
                .walletFromId(transaction.getFromWallet().getId())
                .walletToId(transaction.getToWallet() != null ? transaction.getToWallet().getId() : null)
                .transactionId(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .build();
    }



}

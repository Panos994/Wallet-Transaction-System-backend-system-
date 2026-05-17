package wallet_transaction_system.demo.dto;

import lombok.*;
import wallet_transaction_system.demo.entity.TransactionStatus;
import wallet_transaction_system.demo.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransactionResponseDTO {
    private UUID walletFromId;
    private UUID walletToId;
    private TransactionType type; //transaction type
    private BigDecimal amount;
    private TransactionStatus status; // transaction status

    private UUID transactionId;
    private LocalDateTime createdAt;

}

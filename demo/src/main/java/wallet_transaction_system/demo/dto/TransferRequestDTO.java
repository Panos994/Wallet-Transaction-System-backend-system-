package wallet_transaction_system.demo.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequestDTO {
    private BigDecimal amount;
    private UUID targetWalletId;
}

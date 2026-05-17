package wallet_transaction_system.demo.dto;

import lombok.*;
import wallet_transaction_system.demo.entity.Currency;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWalletResponseDTO {
    private BigDecimal balance;
    private Currency currency;
    private Long version;
}

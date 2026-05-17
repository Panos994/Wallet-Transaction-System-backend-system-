package wallet_transaction_system.demo.dto;

import lombok.*;

import wallet_transaction_system.demo.entity.Currency;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWalletRequestDTO {
    private Currency currency;
}

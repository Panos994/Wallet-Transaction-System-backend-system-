package wallet_transaction_system.demo.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositRequestDTO {
    private BigDecimal amount;
}

package wallet_transaction_system.demo.dto;

import lombok.*;
import wallet_transaction_system.demo.entity.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private String token;
    private Role role;
}

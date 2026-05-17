package wallet_transaction_system.demo.dto;

import lombok.*;
import wallet_transaction_system.demo.entity.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserRequestDTO {
    private String email;
    private String password;

}

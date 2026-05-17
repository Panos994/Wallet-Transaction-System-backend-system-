package wallet_transaction_system.demo.dto;

import lombok.*;
import wallet_transaction_system.demo.entity.Role;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateUserResponseDTO {
    private UUID userId;
    private String email;
    private Role role;
}

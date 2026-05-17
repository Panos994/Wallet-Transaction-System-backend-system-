package wallet_transaction_system.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private String error;
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;
}
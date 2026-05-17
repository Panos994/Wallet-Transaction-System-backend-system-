package wallet_transaction_system.demo.controller;

import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wallet_transaction_system.demo.dto.CreateTransactionResponseDTO;
import wallet_transaction_system.demo.dto.CreateUserRequestDTO;
import wallet_transaction_system.demo.dto.CreateUserResponseDTO;
import wallet_transaction_system.demo.entity.Role;
import wallet_transaction_system.demo.entity.User;
import wallet_transaction_system.demo.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<CreateUserResponseDTO> createUser(@Valid @RequestBody CreateUserRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<Void> assignRole(@PathVariable UUID userId, @RequestParam Role role){
        userService.assignRole(userId, role);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<CreateUserResponseDTO> getUserByEmail(@RequestParam String email){
        return ResponseEntity.status(HttpStatus.OK).body(mapToResponse(userService.findByEmail(email)));
    }

    private CreateUserResponseDTO mapToResponse(User user){
        return CreateUserResponseDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}

package wallet_transaction_system.demo.service;

import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import wallet_transaction_system.demo.dto.CreateUserRequestDTO;
import wallet_transaction_system.demo.dto.CreateUserResponseDTO;
import wallet_transaction_system.demo.entity.Role;
import wallet_transaction_system.demo.entity.User;
import wallet_transaction_system.demo.repository.UserRepository;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public CreateUserResponseDTO createUser(CreateUserRequestDTO dto){
        if(userRepository.existsByEmail(dto.getEmail())){
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);

        return CreateUserResponseDTO.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void assignRole(UUID userId, Role newRole){
        User user = userRepository.findById(userId).orElseThrow(() ->  new IllegalArgumentException("User not found with id: " + userId));
        user.setRole(newRole);
        userRepository.save(user);
    }
}

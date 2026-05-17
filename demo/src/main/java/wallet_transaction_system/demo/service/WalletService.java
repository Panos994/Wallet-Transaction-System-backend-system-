package wallet_transaction_system.demo.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import wallet_transaction_system.demo.dto.CreateWalletRequestDTO;
import wallet_transaction_system.demo.dto.CreateWalletResponseDTO;
import wallet_transaction_system.demo.entity.Currency;
import wallet_transaction_system.demo.entity.User;
import wallet_transaction_system.demo.entity.Wallet;
import wallet_transaction_system.demo.repository.UserRepository;
import wallet_transaction_system.demo.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CreateWalletResponseDTO createWallet(CreateWalletRequestDTO dto, UUID userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setCurrency(dto.getCurrency());
        walletRepository.save(wallet);

        return CreateWalletResponseDTO.builder()
                .currency(wallet.getCurrency())
                .balance(wallet.getBalance())
                .version(wallet.getVersion())
                .build();

    }

    public List<Wallet> getWalletsByUserId(UUID userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return walletRepository.findByUser_Id(userId);
    }

    public Wallet getWalletWithLock(UUID walletId){
        return walletRepository.findByIdWithLock(walletId).orElseThrow(() -> new IllegalArgumentException("Wallet not found with id: " + walletId));
    }
}

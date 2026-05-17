package wallet_transaction_system.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import wallet_transaction_system.demo.dto.CreateUserRequestDTO;
import wallet_transaction_system.demo.dto.CreateWalletRequestDTO;
import wallet_transaction_system.demo.dto.CreateWalletResponseDTO;
import wallet_transaction_system.demo.entity.Wallet;
import wallet_transaction_system.demo.security.CustomUserDetails;
import wallet_transaction_system.demo.service.WalletService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<CreateWalletResponseDTO> createWallet(@RequestBody CreateWalletRequestDTO dto, @AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.createWallet(dto, userDetails.getUser().getId()));
    }


    @GetMapping("/my")
    public ResponseEntity<List<CreateWalletResponseDTO>> getMyWallets(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(
                walletService.getWalletsByUserId(userDetails.getUser().getId())
                        .stream()
                        .map(this::mapToResponse)
                        .toList()
        );
    }


    
    private List<CreateWalletResponseDTO> mapToResponse(List<Wallet> wallets){
        return wallets.stream().map(this::mapToResponse).toList();
    }
    private CreateWalletResponseDTO mapToResponse(Wallet wallet){
        return CreateWalletResponseDTO.builder()
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .build();
    }



}

package wallet_transaction_system.demo.entity;

import jakarta.persistence.*;
import jakarta.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "wallets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    private User user;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Column
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Version  // for activating optimistic locking
    @Column(nullable = false)
    private Long version;

    @OneToMany(mappedBy = "fromWallet", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Transaction> outgoingTransactions;

    @OneToMany(mappedBy = "toWallet",fetch=FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Transaction> ongoingTransactions;
}

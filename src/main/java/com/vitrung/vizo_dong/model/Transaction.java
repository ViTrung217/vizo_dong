package com.vitrung.vizo_dong.model;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;

@Entity 
@Table(name = "transactions")  
@Getter
@Setter

public class Transaction {
    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "Receiver_id")
    private User receiver;

    @Column(nullable = false)
    private Long amount;

    private String message;

    private String type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Transaction(User sender, User receiver, Long amount, String message, String type) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.message = message;
        this.type = type;
    }
}

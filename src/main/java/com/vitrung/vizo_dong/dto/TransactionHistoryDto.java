package com.vitrung.vizo_dong.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionHistoryDto {
    private Long id;
    private Long amount;
    private String message;
    private String type;
    private LocalDateTime createdAt;

    private String senderName;
    private String receiverName;
    private String direction;
    private String amountDisplay;
}

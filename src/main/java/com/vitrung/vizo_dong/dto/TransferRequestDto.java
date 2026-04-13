package com.vitrung.vizo_dong.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransferRequestDto {
    private String receiver;
    private Long amount;
    private String message;
}

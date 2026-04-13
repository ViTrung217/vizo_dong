package com.vitrung.vizo_dong.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TopupRequestDto {
    private String username;
    private Long amount;
}

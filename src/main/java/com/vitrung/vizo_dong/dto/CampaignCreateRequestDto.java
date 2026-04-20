package com.vitrung.vizo_dong.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CampaignCreateRequestDto {
    private String name;
    private String description;
    private Long goalAmount;
}

package com.mysite.stockburning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BullishRankDTO {
    private String ticker;
    private int bullishCnt;
    private int bearishCnt;
}

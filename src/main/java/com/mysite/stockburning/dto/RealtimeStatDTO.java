package com.mysite.stockburning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RealtimeStatDTO {
    private LocalDate date;
    private int count;
    private int bullishCnt;
    private int bearishCnt;
}

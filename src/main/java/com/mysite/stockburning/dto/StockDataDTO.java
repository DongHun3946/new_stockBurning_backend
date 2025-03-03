package com.mysite.stockburning.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockDataDTO {
    private Long id;
    private String stockSymbol;
    private String rsi;
    private String employee;
    private String insiderOwn;
    private String instOwn;
    private String recommend;
}

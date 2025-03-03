package com.mysite.stockburning.controller;

import com.mysite.stockburning.dto.QQQIndexDTO;
import com.mysite.stockburning.dto.SPYIndexDTO;
import com.mysite.stockburning.service.StockApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/stock")
public class StockApiController {

    private final StockApiService stockApiService;

    @GetMapping("/qqq")
    public QQQIndexDTO getStockData1(){
        return stockApiService.getCachedQQQData();
    }
    @GetMapping("/spy")
    public SPYIndexDTO getStockData2(){
        return stockApiService.getCachedSPYData();
    }
}

/*
    클라이언트는 2분마다 /api/stock 에 HTTP 요청을 보냄
    현재 컨트롤러에서는 StockApiService 의 getCachedStockData 메소드를 호출하여 값을 반환함
    getCachedStockData에서는 캐시를 이용하여 값을 반환함.
*/
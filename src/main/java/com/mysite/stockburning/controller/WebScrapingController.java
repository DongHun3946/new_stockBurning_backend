package com.mysite.stockburning.controller;

import com.mysite.stockburning.dto.BankRateDTO;
import com.mysite.stockburning.dto.StockDataDTO;
import com.mysite.stockburning.entity.BankRates;
import com.mysite.stockburning.service.BankRateService;
import com.mysite.stockburning.service.PostService;
import com.mysite.stockburning.service.WebScrapingService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/scrape")
@RequiredArgsConstructor
public class WebScrapingController {

    private final BankRateService bankRateService;
    @GetMapping("/bank-rates")
    public List<BankRates> getBankRates(){
        return bankRateService.getAllBankRates();
    }
}
/*
ResponseEntity<String>: 메서드의 반환 타입은 ResponseEntity입니다.
이는 HTTP 응답을 나타내며, 응답 상태 코드, 헤더, 바디를 포함할 수 있습니다.

@RequestBody StockDataDTO stockDataDTO:
@RequestBody는 클라이언트가 요청 본문에 포함한 JSON 데이터를
자동으로 StockDataDTO 객체로 변환합니다.
즉, 클라이언트가 POST 요청으로 보내는 JSON이 stockDataDTO 객체로 매핑됩니다.

ResponseEntity.ok()는 HTTP 상태 코드 200(성공)을 포함한 응답을 반환합니다.
*/
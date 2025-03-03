package com.mysite.stockburning.controller;

import com.mysite.stockburning.dto.*;
import com.mysite.stockburning.entity.StockTickers;
import com.mysite.stockburning.service.PostService;
import com.mysite.stockburning.service.StockOpinionService;
import com.mysite.stockburning.service.StockSearchService;
import com.mysite.stockburning.service.WebScrapingService;
import com.mysite.stockburning.service.kafka.SearchTickerProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockSearchController {

    private final StockSearchService stockSearchService;
    private final WebScrapingService webScrapingService;
    private final SearchTickerProducer searchTickerProducer;
    private final PostService postService;
    private final StockOpinionService stockOpinionService;

    @GetMapping("/suggestions") //http://localhost:8080/api/stock/suggestions?prefix=AA
    public List<StockTickers> getStockSuggestions(@RequestParam("prefix") String prefix) {
        return stockSearchService.getSuggestions(prefix);
    }

    //http://localhost:8080/api/stock?ticker=AAPL&type=allPost
    @GetMapping
    public ResponseEntity<?> getStockByTicker(@RequestParam(value = "ticker", defaultValue = "nasdaq") String ticker,
                                              @RequestParam(value = "type", defaultValue = "allPost") String type) {
        try {
            List<PostDTO> postDTOS;
            if ("nasdaq".equals(ticker)) {
                if ("bestPost".equals(type)) {
                    postDTOS = postService.readBestPost("null");
                } else {
                    log.info("기본 게시글 출력");
                    postDTOS = postService.readDefault();
                }
                return ResponseEntity.ok(postDTOS);
            } else {
                Optional<StockTickers> stockTickers = stockSearchService.getStockByTicker(ticker);
                if (stockTickers.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
                StockTickers stockTicker = stockTickers.get();
                if ("bestPost".equals(type)) {
                    postDTOS = postService.readBestPost("null");
                } else {
                    postDTOS = postService.read(stockTicker);
                }

                StockDataDTO stockDataDTO = webScrapingService.scrapeStockData(stockTicker);
                LocalDate date = LocalDate.now();

                int postCount = stockOpinionService.getRealtimePostCount(stockTicker.getStockSymbol());
                int bullishCnt = stockOpinionService.getRealtimeBullishOpinion(stockTicker.getStockSymbol());
                int bearishCnt = stockOpinionService.getRealtimeBearishOpinion(stockTicker.getStockSymbol());

                RealtimeStatDTO realtimeStatDTO = new RealtimeStatDTO(date, postCount, bullishCnt, bearishCnt);
                List<WeeklyPostCountDTO> weeklyPostCountDTO = WeeklyPostCountDTO.of(stockOpinionService.getWeeklyPostCount(stockTicker.getStockSymbol()), realtimeStatDTO);
                DataBundleDTO stockPostDTO = new DataBundleDTO(stockDataDTO, postDTOS, realtimeStatDTO, weeklyPostCountDTO);

                return ResponseEntity.ok(stockPostDTO);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("StockSearchController 에서 오류 발생");
        }
    }
    @PostMapping("/search")
    public ResponseEntity<?> getSearchRank(@RequestParam("ticker") String ticker){
        try{
            Optional<StockTickers> stockTickers = stockSearchService.getStockByTicker(ticker);
            if(stockTickers.isPresent()){
                searchTickerProducer.sendSearchQuery(ticker);
                return ResponseEntity.ok().build();
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("존재하지 않는 티커입니다.");
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("검색량순위 기능 에러 발생");
        }
    }
}

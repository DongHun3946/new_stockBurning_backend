package com.mysite.stockburning.controller.kafka;

import com.mysite.stockburning.dto.BullishRankDTO;
import com.mysite.stockburning.dto.SearchRankDTO;
import com.mysite.stockburning.service.StockOpinionService;
import com.mysite.stockburning.service.kafka.SearchTickerRank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SearchTickerController {
    private final SearchTickerRank searchTickerRank;
    private final StockOpinionService stockOpinionService;
    @GetMapping("/search/top5")
    public ResponseEntity<?> getTop5Tickers() {
        Map<String, Double> top5TickersSet = searchTickerRank.getTopSearchesAndCount();

        if (top5TickersSet.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        // 티커와 검색 수를 포함하는 객체로 변환
        List<SearchRankDTO> result = top5TickersSet.entrySet().stream()
                .map(entry -> new SearchRankDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
    @GetMapping("/bullish/top3")
    public ResponseEntity<?> getBullishTop3Tickers() {
        Map<String, Map<String, Integer>> top3BullishTickers = stockOpinionService.getTop3BullishTickers();

        if (top3BullishTickers.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        List<BullishRankDTO> result = top3BullishTickers.entrySet().stream()
                .map(entry -> new BullishRankDTO(
                        entry.getKey(),
                        entry.getValue().getOrDefault("bullishCnt", 0),
                        entry.getValue().getOrDefault("bearishCnt", 0)))
                .toList();

        return ResponseEntity.ok(result);
    }
    @GetMapping("/fear-greed-index")
    public ResponseEntity<?> getFearAndGreedIndex(){
       double score = stockOpinionService.calculateFearGreedIndex();
       return ResponseEntity.ok(score);
    }
}
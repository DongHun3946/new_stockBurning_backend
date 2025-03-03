package com.mysite.stockburning.service.kafka;

import com.mysite.stockburning.service.StockOpinionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SearchTickerRank {
    private final StringRedisTemplate redisTemplate;
    private final StockOpinionService stockOpinionService;
    private final static String SEARCH_RANKING_KEY = "stock_search_ranking";
    private final static String POST_COUNT_KEY = "stock_post_count";
    private final static String BULLISH_OPINION = "stock_bullish_opinion";
    private final static String BEARISH_OPINION = "stock_bearish_opinion";
    public void increaseSearchCount(String ticker){
        redisTemplate.opsForZSet().incrementScore(SEARCH_RANKING_KEY, ticker, 1);
    }
    public Map<String, Double> getTopSearchesAndCount(){
        Set<ZSetOperations.TypedTuple<String>> topSearchesWithScores = redisTemplate.opsForZSet().reverseRangeWithScores(SEARCH_RANKING_KEY, 0, 4);

        Map<String, Double> result = new LinkedHashMap<>();
        for (ZSetOperations.TypedTuple<String> tuple : topSearchesWithScores) {
            result.put(tuple.getValue(), tuple.getScore());
        }

        return result;
    }
    @Scheduled(cron = "0 0 * * * ?") //1시간마다 초기화
    public void resetSearchRanking(){
        redisTemplate.opsForZSet().removeRange(SEARCH_RANKING_KEY, 0, -1);
    }
}

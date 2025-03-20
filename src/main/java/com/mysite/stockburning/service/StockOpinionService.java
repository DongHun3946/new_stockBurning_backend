package com.mysite.stockburning.service;

import com.mysite.stockburning.entity.StockOpinionStats;
import com.mysite.stockburning.entity.StockTickers;
import com.mysite.stockburning.repository.StockOpinionRepository;
import com.mysite.stockburning.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockOpinionService {
    private final StringRedisTemplate redisTemplate;
    private final StockOpinionRepository stockOpinionRepository;
    private final StockSearchService stockSearchService;
    private final static String POST_COUNT_KEY = "stock_post_count";
    private final static String BULLISH_OPINION = "stock_bullish_opinion";
    private final static String BEARISH_OPINION = "stock_bearish_opinion";

    @Scheduled(cron = "0 0 0 * * ?")
    public void initialDailyData() {
        log.info("모든 티커의 redis 값 초기화 시작");
        try {
            List<StockTickers> allStockEntity = stockSearchService.getAllStock();
            List<String> allTickers = new ArrayList<>();
            for (StockTickers tickers : allStockEntity) {
                allTickers.add(tickers.getStockSymbol());
            }
            for (String ticker : allTickers) {
                redisTemplate.opsForZSet().add(POST_COUNT_KEY, ticker, 0); //모든 티커의 POST_COUNT_KEY 값 초기화
                redisTemplate.opsForZSet().add(BULLISH_OPINION, ticker, 0);
                redisTemplate.opsForZSet().add(BEARISH_OPINION, ticker, 0);
            }
        } catch (Exception e) {
            log.error("initialDailyData(0 메소드 에러 발생 : ", e);
        }
        log.info("모든 티커의 redis 값 초기화 완료");
    }

    //자정을 기준으로 게시글, 상승, 하락 의견이 있는 티커는 mysql 에 저장 후 redis 에 있는 데이터 삭제
    @Scheduled(cron = "0 59 23 * * ?") //초(0) 분(0) 시(0) 일(*) 월(*) 요일(?)
    public void collectDailyData() {
        log.info("일일 게시글, 상승/하락 의견 데이터 수집 시작");
        List<StockTickers> allStockEntity = stockSearchService.getAllStock(); //
        for (StockTickers stockTickers : allStockEntity) {
            try {
                log.info("수집할 종목 리스트 : {}", stockTickers.getStockSymbol());
                int postCount = getRealtimePostCount(stockTickers.getStockSymbol());
                int bullishCnt = getRealtimeBullishOpinion(stockTickers.getStockSymbol());
                int bearishCnt = getRealtimeBearishOpinion(stockTickers.getStockSymbol());

                StockOpinionStats opinionStats = StockOpinionStats.of(stockTickers, bullishCnt, bearishCnt, postCount);
                stockOpinionRepository.save(opinionStats);
                clearAllPostCount();
                clearAllOpinion();
            } catch (Exception e) {
                log.error("collectDailyData() 메소드 에러 발생 : ", e);
            }
        }
        log.info("일일 게시글, 상승/하락 의견 데이터 수집 완료");
    }

    public Map<String, Map<String, Integer>> getWeeklyPostCount(String ticker) {
        Map<String, Map<String, Integer>> postCount = new LinkedHashMap<>(); //HashMap<>과 달리 삽입된 순서 유지

        LocalDate today = LocalDate.now();

        Optional<StockTickers> tickerObject = stockSearchService.getStockByTicker(ticker);
        if (tickerObject.isPresent()) {
            StockTickers stockTicker = tickerObject.get();

            for (int i = 6; i > 0; i--) {
                LocalDate date = today.minusDays(i);
                StockOpinionStats stockOpinionStats = stockOpinionRepository.findByStockTickersAndCreatedAt(stockTicker, date);

                Map<String, Integer> stats = new HashMap<>();
                stats.put("count", stockOpinionStats.getPostCount());
                stats.put("bullishCnt", stockOpinionStats.getBullishCnt());
                stats.put("bearishCnt", stockOpinionStats.getBearishCnt());

                postCount.put(date.toString(), stats);
            }
            return postCount;
        } else {
            return null;
        }
    }

    public Map<String, Map<String, Integer>> getTop3BullishTickers() {
        try {
            Map<String, Map<String, Integer>> result = new LinkedHashMap<>();
            Set<String> topTickers = redisTemplate.opsForZSet().reverseRange(BULLISH_OPINION, 0, 2);

            if (topTickers.isEmpty()) {
                return result;
            }

            for (String ticker : topTickers) {
                Map<String, Integer> tickerData = new LinkedHashMap<>();
                tickerData.put("bullishCnt", getRealtimeBullishOpinion(ticker));
                tickerData.put("bearishCnt", getRealtimeBearishOpinion(ticker));
                result.put(ticker, tickerData);
            }
            return result;
        } catch (Exception e) {
            log.error("getTop3BullishTickers() 메소드 에러 발생 : ", e);
            return Collections.emptyMap();
        }
    }


    public void setRealtimePostCount(String ticker) {
        redisTemplate.opsForZSet().incrementScore(POST_COUNT_KEY, ticker, 1);
    }

    public void setRealtimePostCount() {
        redisTemplate.opsForZSet().incrementScore(POST_COUNT_KEY, "Nasdaq", 1);
    }

    public int getRealtimePostCount(String ticker) {
        Double score = redisTemplate.opsForZSet().score(POST_COUNT_KEY, ticker);
        return (score != null) ? score.intValue() : 0;
    }

    public void clearAllPostCount() {
        redisTemplate.opsForZSet().removeRange(POST_COUNT_KEY, 0, -1);
        log.info("Redis 에서 모든 티커의 게시글 데이터 수를 삭제했습니다.");
    }

    public void setRealtimeBullishOpinion(String ticker) {
        redisTemplate.opsForZSet().incrementScore(BULLISH_OPINION, ticker, 1);
    }

    public void setRealtimeBullishOpinion() {
        redisTemplate.opsForZSet().incrementScore(BULLISH_OPINION, "Nasdaq", 1);
    }

    public int getRealtimeBullishOpinion(String ticker) {
        Double score = redisTemplate.opsForZSet().score(BULLISH_OPINION, ticker);
        return (score != null) ? score.intValue() : 0;
    }

    public void setRealtimeBearishOpinion(String ticker) {
        redisTemplate.opsForZSet().incrementScore(BEARISH_OPINION, ticker, 1);
    }

    public void setRealtimeBearishOpinion() {
        redisTemplate.opsForZSet().incrementScore(BEARISH_OPINION, "Nasdaq", 1);
    }

    public int getRealtimeBearishOpinion(String ticker) {
        Double score = redisTemplate.opsForZSet().score(BEARISH_OPINION, ticker);
        return (score != null) ? score.intValue() : 0;
    }

    public Map<Integer, Integer> getRealtimeBullishAndBearishOpinion(String ticker){
        Double bullish = redisTemplate.opsForZSet().score(BULLISH_OPINION, ticker);
        Double bearish = redisTemplate.opsForZSet().score(BEARISH_OPINION, ticker);
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(bullish.intValue(), bearish.intValue());
        return map;
    }
    public int getTotalBullishOpinions() {
        // 모든 티커의 상승 의견 점수를 가져오기
        Set<ZSetOperations.TypedTuple<String>> result = redisTemplate.opsForZSet().rangeWithScores(BULLISH_OPINION, 0, -1);

        int totalBullishOpinions = 0;

        if (result != null) {
            for (ZSetOperations.TypedTuple<String> tuple : result) {
                // 각 티커의 상승 의견 수를 더함
                totalBullishOpinions += tuple.getScore().intValue();
            }
        }

        return totalBullishOpinions;
    }
    public int getTotalBearishOpinions() {
        // 모든 티커의 상승 의견 점수를 가져오기
        Set<ZSetOperations.TypedTuple<String>> result = redisTemplate.opsForZSet().rangeWithScores(BEARISH_OPINION, 0, -1);

        int totalBearishOpinions = 0;

        if (result != null) {
            for (ZSetOperations.TypedTuple<String> tuple : result) {
                // 각 티커의 상승 의견 수를 더함
                totalBearishOpinions += tuple.getScore().intValue();
            }
        }

        return totalBearishOpinions;
    }
    public int getTotalPostCount() {
        // 모든 티커의 상승 의견 점수를 가져오기
        Set<ZSetOperations.TypedTuple<String>> result = redisTemplate.opsForZSet().rangeWithScores(POST_COUNT_KEY, 0, -1);

        int totalPostCount = 0;

        if (result != null) {
            for (ZSetOperations.TypedTuple<String> tuple : result) {
                // 각 티커의 상승 의견 수를 더함
                totalPostCount += tuple.getScore().intValue();
            }
        }

        return totalPostCount;
    }
    public int calculateFearGreedIndex() {
        // 게시글 수 (stock discussion volume) - 높을수록 탐욕적
        int postCount = getTotalPostCount();

        // 상승 의견 수 (bullish opinions) - 높을수록 탐욕적
        int bullishOpinion = getTotalBullishOpinions();

        // 하락 의견 수 (bearish opinions) - 높을수록 공포적
        int bearishOpinion = getTotalBearishOpinions();

        // 가중치 설정 (상대적으로 중요도에 맞춰 가중치 설정)
        double postCountWeight = 0.2;    // 게시글 수의 가중치
        double bullishWeight = 0.4;      // 상승 의견의 가중치
        double bearishWeight = 0.4;      // 하락 의견의 가중치

        // 공포탐욕 지수 계산
        double index = (postCount * postCountWeight) +
                (bullishOpinion * bullishWeight) -
                (bearishOpinion * bearishWeight);

        // 0~100 범위로 지수 조정
        double scaledIndex = Math.min(100, Math.max(0, index));
        return (int)scaledIndex;
    }

    public void clearAllOpinion() {
        redisTemplate.opsForZSet().removeRange(BULLISH_OPINION, 0, -1);
        redisTemplate.opsForZSet().removeRange(BEARISH_OPINION, 0, -1);
        log.info("Redis 에서 모든 티커의 유저 상승/하락 의견 데이터 수를 삭제했습니다.");
    }
}

package com.mysite.stockburning.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchTickerConsumer {
    private final SearchTickerRank searchTickerRank;

    @KafkaListener(topics = "stock_search_topic", groupId = "search-consumer-group1")
    public void consumeSearchQuery(String ticker) { //Consumer 가 메시지를 받아 Redis 에서 검색 횟수 증가
        searchTickerRank.increaseSearchCount(ticker);
    }
}

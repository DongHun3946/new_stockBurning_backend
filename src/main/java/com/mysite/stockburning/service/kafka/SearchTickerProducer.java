package com.mysite.stockburning.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchTickerProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "stock_search_topic";

    @Async
    public void sendSearchQuery(String ticker){
        try{
            kafkaTemplate.send(TOPIC, ticker);
        }catch(Exception e){
            log.error("kafka producer error : ", e);
        }
    }
}
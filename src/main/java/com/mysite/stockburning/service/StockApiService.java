package com.mysite.stockburning.service;

import com.mysite.stockburning.dto.QQQIndexDTO;
import com.mysite.stockburning.dto.SPYIndexDTO;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StockApiService {
    private static final Logger logger = LoggerFactory.getLogger(StockApiService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiKey = "ctkkv0hr01qntkqpd110ctkkv0hr01qntkqpd11g";
    private QQQIndexDTO cachedQQQData = null;
    private SPYIndexDTO cachedSPYData = null;

    //2분마다 API를 호출하여 가격정보를 cachedStockData에 저장
    @Scheduled(fixedRate = 120000)
    public void fetchStockData(){
        try{
            String url1 = "https://finnhub.io/api/v1/quote?symbol=QQQ&token=" + apiKey;
            this.cachedQQQData = restTemplate.getForObject(url1, QQQIndexDTO.class); //HTTP GET 요청을 보내고 응답을 받아서 StockIndexDTO 객체로 변환한 후 반환

            String url2 = "https://finnhub.io/api/v1/quote?symbol=SPY&token=" + apiKey;
            this.cachedSPYData = restTemplate.getForObject(url2, SPYIndexDTO.class); //HTTP GET 요청을 보내고 응답을 받아서 StockIndexDTO 객체로 변환한 후 반환
        }catch(Throwable e){
            logger.error("QQQ, SPY 지수 호출에 오류 발생", e);
        }
    }
    //value = 캐시이름, key = 캐시 키
    @CachePut(value="stockCache", key="'stockIndexQQQ'")
    public QQQIndexDTO getCachedQQQData(){
        return cachedQQQData;
    }

    @CachePut(value="stockCache", key="'stockIndexSPY'")
    public SPYIndexDTO getCachedSPYData(){
        return cachedSPYData;
    }
}
/*
  2분마다 해당 url에서 API를 호출하여 cachedStockData에 저장
  클라이언트가 서버에 /api/stock 으로 HTTP 요청을 보내면 cachedStockData 값을 반환
*/

package com.mysite.stockburning.service;

import com.mysite.stockburning.dto.BankRateDTO;
import com.mysite.stockburning.dto.StockDataDTO;
import com.mysite.stockburning.entity.BankRates;
import com.mysite.stockburning.entity.StockTickers;
import com.mysite.stockburning.repository.BankRateRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WebScrapingService {
    private static final Logger logger = LoggerFactory.getLogger(WebScrapingService.class);
    private final BankRateRepository bankRateRepository;
    @Scheduled(cron = "0 0 0,3,6,9,12,15,18,21 * * ?") //초(0) 분(0) 시(0,3,6,9,12) 일(*) 월(*) 요일(?)
    public void bankRates() throws IOException {
        String url = "https://kr.investing.com/central-banks/";
        Document document = null;
        List<BankRateDTO> bankRateList = null;
        try{
            document = Jsoup.connect(url).get();
        }catch(Throwable e){
            logger.error("investing.com 으로부터 *접근* 도중 오류 발생");
        }

        try{
            Element table = document.getElementById("curr_table"); //id = "curr_table"
            Elements rows = table.select("tbody tr"); //<tbody>-<tr>
            bankRateList = new ArrayList<>();
                   //DTO 리스트 생성
            for (Element row : rows) {
                String country = row.select("td.bold.left.noWrap a").text();       // 국가 이름
                String bankTicker = row.select("td.bold.left.noWrap span").text(); //중앙은행 티커
                String currentRate = row.select("td").get(2).text();               // 현재 금리
                String nextMeeting = row.select("td").get(3).text();              // 다음 회의

                bankTicker = bankTicker.replaceAll("[()]", "");
                BankRateDTO dto = new BankRateDTO(country, bankTicker, currentRate, nextMeeting);
                bankRateList.add(dto);
            }
        }
        catch(Throwable e){
            logger.error("investing.com 으로부터 *데이터 스크래핑* 도중 오류 발생");
        }
        for(BankRateDTO dto : bankRateList){
            BankRates bankRates = BankRates.builder()
                    .country(dto.getCountry())
                    .bank(dto.getBankTicker())
                    .rate(dto.getCurrentRate())
                    .nextMeeting(dto.getNextMeeting())
                    .build();
            this.bankRateRepository.save(bankRates);
        }
    }

    public StockDataDTO scrapeStockData(StockTickers stockTickers) throws IOException {

        String url = "https://finviz.com/quote.ashx?t=" + stockTickers.getStockSymbol() + "&p=d";
        Document document = null;
        StockDataDTO dataDTO = new StockDataDTO();
        try {
            document = Jsoup.connect(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .timeout(5000)
                    .get();
        } catch (Throwable e) {
            logger.error("finviz로부터 티커:{} 웹스크래핑 *접근* 도중 오류 발생 : {}", stockTickers.getStockSymbol(), e.getMessage());
        }

        try {
            Element table = document.select("table.js-snapshot-table.snapshot-table2.screener_snapshot-table-body").first();
            if (table != null) {
                Elements rows = table.select("tbody tr");
                Element Row_9 = rows.get(8);                             // 9번째 tr 선택
                Element rsi_td = Row_9.select("td").get(9);   // 10번째 td 선택
                Element rsi = rsi_td.select("b").first();    //span 태그 선택

                //직원 수
                Element Row_emp = rows.get(9);
                Element employ_td = Row_emp.select("td").get(1); //8번째 td 선택
                Element employ = employ_td.select("b").first();

                //내부자 보유율
                Element Row_1 = rows.get(0);
                Element insider_td = Row_1.select("td").get(7); //8번째 td 선택
                Element insider = insider_td.select("b").first();

                //기관 보유율
                Element Row_3 = rows.get(2);
                Element inst_td = Row_3.select("td").get(7);
                Element inst = inst_td.select("b").first();

                //애널리스트 추천
                Element Row_10 = rows.get(9);
                Element recommend_td = Row_10.select("td").get(9);
                Element recommend = recommend_td.select("b").first();

                dataDTO.setId(stockTickers.getId());
                dataDTO.setStockSymbol(stockTickers.getStockSymbol());
                if (rsi != null) {
                    dataDTO.setRsi(rsi.text());
                }
                if (employ != null) {
                    dataDTO.setEmployee(employ.text());
                }
                if (insider != null) {
                    dataDTO.setInsiderOwn(insider.text());
                }
                if (inst != null) {
                    dataDTO.setInstOwn(inst.text());
                }
                if (recommend != null) {
                    dataDTO.setRecommend(recommend.text());
                }
            }
        }catch(Throwable e){
            logger.error("finviz로부터 티커:{} 웹스크래핑 *데이터 스크래핑* 도중 오류가 발생하였음 : {}", dataDTO.getStockSymbol(), e.getMessage());
        }
        return dataDTO;
    }
}

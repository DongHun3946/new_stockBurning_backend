package com.mysite.stockburning;

import com.mysite.stockburning.entity.*;
import com.mysite.stockburning.repository.CommentRepository;
import com.mysite.stockburning.repository.PostRepository;
import com.mysite.stockburning.repository.StockOpinionRepository;
import com.mysite.stockburning.repository.UserRepository;
import com.mysite.stockburning.service.S3Service;
import com.mysite.stockburning.service.StockSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@SpringBootTest(classes = StockBurningApplicationTests.class)
class StockBurningApplicationTests {

    @Autowired
    private StockOpinionRepository stockOpinionRepository;

    @Autowired
    private StockSearchService stockSearchService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private S3Service s3Service;

    @Test
    public void create() {
        Random random = new Random();

        // Optional 체크 후 안전하게 처리
        List<StockTickers> stockTickerOpt = stockSearchService.getAllStock();


        for(StockTickers ticker : stockTickerOpt){

            for (int i = 12; i <= 19; i++) {
                LocalDate date = LocalDate.of(2025, 3, i);

                StockOpinionStats stats = StockOpinionStats.builder()
                        .stockTickers(ticker)
                        .bullishCnt(random.nextInt(100))  // Random 객체 재사용
                        .bearishCnt(random.nextInt(100))
                        .postCount(random.nextInt(500))
                        .createdAt(date)  // 날짜 설정
                        .build();
                stockOpinionRepository.save(stats);

            }
        }
    }


}
 /*
        // Random 객체를 반복문 외부에서 생성 (성능 개선)


         */
package com.mysite.stockburning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching     //캐시를 사용하기 위함
@EnableScheduling  //정해진 시간마다 스케줄링하기 위함
@EnableJpaAuditing //@CreatedDate 와 @LastModifiedDate 를 사용하기 위함
public class StockBurningApplication {
	public static void main(String[] args) {
		SpringApplication.run(StockBurningApplication.class, args);
	}
}



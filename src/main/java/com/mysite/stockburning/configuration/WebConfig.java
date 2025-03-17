package com.mysite.stockburning.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOrigins("https://www.stockburning.shop")  // 클라이언트 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("Authorization", "Content-Type", "Accept")
                .allowCredentials(true)  //클라이언트가 쿠키 또는 인증정보를 요청에 포함할 수 있음
                .exposedHeaders(HttpHeaders.LOCATION, "Authorization"); //클라이언트가 응답헤더를 접근할 수 있도록 함
    }
}

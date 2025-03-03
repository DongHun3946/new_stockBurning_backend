package com.mysite.stockburning.controller.OAuth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.stockburning.service.OAuth2.KakaoOauth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor

public class KakaoLoginController {
    private final KakaoOauth2Service kakaoOauth2Service;

    /*
    @GetMapping("/api/kakao/login/url")
    public ResponseEntity<?> getKakaoLoginUrl(){
        String loginUrl = kakaoOauth2Service.getKakaoLoginUrl();
        return ResponseEntity.ok(Collections.singletonMap("url", loginUrl));
    }*/



    @GetMapping("/oauth/kakao/authorization")
    public void getKakaoAccessToken(@RequestParam("code") String code){ //인가코드를 받아서 accessToken 요청
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(code);
        String tokenUrl = kakaoOauth2Service.getTokenUri(); //"https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoOauth2Service.getRestApiKey());
        params.add("redirect_uri", kakaoOauth2Service.getRedirectUrl());
        params.add("code", code);
        params.add("client_secret", kakaoOauth2Service.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                tokenUrl, // 요청할 서버 주소
                HttpMethod.POST, // 요청할 방식
                request, // 요청할 때 보낼 데이터
                String.class // 요청 시 반환되는 데이터 타입
        );
        //restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Void.class);

    }
}


 /*
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);
        System.out.println(response);
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String accessToken = jsonNode.get("access_token").asText();
            System.out.println("액세스 토큰 : " + accessToken);
            System.out.println("회원정보 : "  + kakaoOauth2Service.getUserInfo(accessToken));
            return kakaoOauth2Service.getUserInfo(accessToken);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

         */
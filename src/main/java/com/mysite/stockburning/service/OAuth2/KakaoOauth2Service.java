
package com.mysite.stockburning.service.OAuth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
@Getter
public class KakaoOauth2Service {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    String restApiKey;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    String redirectUrl;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    String clientSecret;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    String tokenUri;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    String userInfoUri;

    /*

    URI baseUri = URI.create("https://kauth.kakao.com/oauth/authorize");
    public String getKakaoLoginUrl(){                       //카카오 로그인 페이지로 이동하기
        String authUrl = UriComponentsBuilder.fromUri(baseUri)
                .queryParam("response_type","code")
                .queryParam("client_id", restApiKey)
                .queryParam("redirect_uri", redirectUrl)
                .toUriString();

        return authUrl;
    }

    public ResponseEntity<?> getUserInfo(String accessToken) { //액세스 토큰으로 회원정보 얻기
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, String.class);
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            Long id = jsonNode.get("id").asLong();
            String nickname = jsonNode.path("properties").path("nickname").asText();
            String profile_image = jsonNode.path("properties").path("profile_image").asText();
            return ResponseEntity.ok(Map.of("id", id, "nickname", nickname, "profile_image", profile_image));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
    */

}
/*

<200 OK OK,{"id":3930040613,"connected_at":"2025-02-20T12:46:51Z",
            "properties":{"nickname":"최동훈","profile_image":"http://img1.kakaocdn.net/thumb/R640x640.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg","thumbnail_image":"http://img1.kakaocdn.net/thumb/R110x110.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg"},
            "kakao_account":{"profile_nickname_needs_agreement":false,"profile_image_needs_agreement":false,"profile":{"nickname":"최동훈","thumbnail_image_url":"http://img1.kakaocdn.net/thumb/R110x110.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg","profile_image_url":"http://img1.kakaocdn.net/thumb/R640x640.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg","is_default_image":true,"is_default_nickname":false}}},[Date:"Thu, 20 Feb 2025 13:21:20 GMT", Server:"Apache", Access-Control-Allow-Origin:"*", Access-Control-Allow-Methods:"GET, POST, PUT, DELETE, OPTIONS", Access-Control-Allow-Headers:"Content-Type,X-Requested-With,Accept,Authorization,Origin,KA,Cache-Control,Pragma", X-Request-ID:"d5b4943c-73b4-4af4-9af3-950a59593c80", Quota-Type:"INC_AND_CHECK", Content-Type:"application/json;charset=UTF-8", Content-Length:"801", Keep-Alive:"timeout=10, max=500", Connection:"Keep-Alive"]>
 */